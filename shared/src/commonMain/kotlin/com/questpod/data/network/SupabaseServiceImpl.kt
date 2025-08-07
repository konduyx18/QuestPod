package com.questpod.data.network

import com.questpod.domain.model.Story
import com.questpod.domain.model.StoryNode
import com.questpod.domain.model.StoryProgress
import com.questpod.domain.model.UserPreferences
import com.questpod.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Implementation of the SupabaseService interface for interacting with Supabase API.
 *
 * @param httpClient The Ktor HttpClient for making API requests
 * @param baseUrl The base URL of the Supabase API
 * @param apiKey The Supabase API key
 */
class SupabaseServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val apiKey: String
) : SupabaseService {

    // Authentication
    
    /**
     * Serializable data class for magic link request
     */
    @Serializable
    private data class MagicLinkRequest(
        val email: String,
        @SerialName("create_user")
        val createUser: Boolean = true,
        @SerialName("redirect_to")
        val redirectTo: String? = "questpod://login-callback", // Deep link for app to handle return from email
        @SerialName("should_create_user")
        val shouldCreateUser: Boolean = true
    )
    
    /**
     * Serializable data class for auth response
     */
    @Serializable
    private data class AuthResponse(
        val user: UserData? = null,
        val session: SessionData? = null,
        @SerialName("access_token")
        val accessToken: String? = null,
        @SerialName("refresh_token")
        val refreshToken: String? = null,
        @SerialName("expires_in")
        val expiresIn: Int? = null,
        @SerialName("expires_at")
        val expiresAt: Int? = null,
        val error: String? = null,
        @SerialName("error_description")
        val errorDescription: String? = null
    )
    
    /**
     * Serializable data class for user data
     */
    @Serializable
    private data class UserData(
        val id: String,
        val email: String?
    )
    
    /**
     * Serializable data class for session data
     */
    @Serializable
    private data class SessionData(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("expires_at")
        val expiresAt: Int,
        val user: UserData
    )
    
    /**
     * Session storage to persist authentication tokens
     */
    private var currentSession: SessionData? = null
    
    /**
     * Serializable data class for token refresh request
     */
    @Serializable
    private data class RefreshTokenRequest(
        @SerialName("refresh_token")
        val refreshToken: String
    )
    
    /**
     * Checks if the current session token is expired and refreshes it if needed
     * @return true if session is valid (either refreshed or not expired), false otherwise
     */
    private suspend fun ensureValidSession(): Boolean {
        val session = currentSession ?: return false
        
        // Check if token is expired (with 60 second buffer)
        val currentTimeSeconds = (System.currentTimeMillis() / 1000).toInt()
        if (session.expiresAt - currentTimeSeconds > 60) {
            // Token is still valid
            return true
        }
        
        // Token is expired or about to expire, refresh it
        return try {
            val response = httpClient.post("$baseUrl/auth/v1/token?grant_type=refresh_token") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                }
                setBody(RefreshTokenRequest(session.refreshToken))
            }
            
            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponse>()
                if (authResponse.accessToken != null && authResponse.refreshToken != null) {
                    // Update the session with new tokens
                    currentSession = SessionData(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken,
                        expiresIn = authResponse.expiresIn ?: 3600,
                        expiresAt = authResponse.expiresAt ?: (currentTimeSeconds + (authResponse.expiresIn ?: 3600)),
                        user = session.user // Keep the same user data
                    )
                    true
                } else {
                    // Invalid response format
                    currentSession = null
                    false
                }
            } else {
                // Failed to refresh token
                currentSession = null
                false
            }
        } catch (e: Exception) {
            // Error refreshing token
            currentSession = null
            false
        }
    }

    override suspend fun signInWithMagicLink(email: String): Result<Unit> {
        return try {
            val response = httpClient.post("$baseUrl/auth/v1/magiclink") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                }
                setBody(MagicLinkRequest(email))
            }
            
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to send magic link: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error sending magic link: ${e.message}", e))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        // If no session exists, consider it already signed out
        if (currentSession == null) {
            return Result.Success(Unit)
        }
        
        return try {
            val response = httpClient.post("$baseUrl/auth/v1/logout") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                    append("Authorization", "Bearer ${currentSession?.accessToken}")
                }
            }
            
            // Clear the session regardless of response
            currentSession = null
            
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                // Even if the server request fails, we've cleared the local session
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            // Even if there's an exception, clear the local session
            currentSession = null
            Result.Success(Unit)
        }
    }

    override suspend fun getCurrentUser(): Result<UserProfile?> {
        // If we don't have a session, return null (not logged in)
        if (currentSession == null) {
            return Result.Success(null)
        }
        
        // Ensure we have a valid session token before proceeding
        if (!ensureValidSession()) {
            return Result.Success(null) // Session expired and couldn't be refreshed
        }
        
        return try {
            val response = httpClient.get("$baseUrl/auth/v1/user") {
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                    append("Authorization", "Bearer ${currentSession?.accessToken}")
                }
            }
            
            if (response.status.isSuccess()) {
                val userData = response.body<UserData>()
                
                // Convert to UserProfile domain model
                val userProfile = UserProfile(
                    id = UUID.fromString(userData.id),
                    email = userData.email ?: "",
                    displayName = "", // This would come from a user_profiles table query
                    bio = "",
                    avatarUrl = "",
                    createdAt = null,
                    updatedAt = null
                )
                
                Result.Success(userProfile)
            } else if (response.status == HttpStatusCode.Unauthorized) {
                // Session expired or invalid and refresh failed
                currentSession = null
                Result.Success(null)
            } else {
                Result.Error(Exception("Failed to get current user: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error getting current user: ${e.message}", e))
        }
    }

    // User Profiles

    override suspend fun getUserProfile(userId: UUID): Result<UserProfile?> {
        return Result.Error(NotImplementedError("getUserProfile not implemented yet"))
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        return Result.Error(NotImplementedError("updateUserProfile not implemented yet"))
    }

    // Stories

    override suspend fun getPublicStories(limit: Int, offset: Int): Result<List<Story>> {
        return Result.Error(NotImplementedError("getPublicStories not implemented yet"))
    }

    override suspend fun getUserStories(userId: UUID): Result<List<Story>> {
        return Result.Error(NotImplementedError("getUserStories not implemented yet"))
    }

    override suspend fun getStory(storyId: UUID): Result<Story?> {
        return Result.Error(NotImplementedError("getStory not implemented yet"))
    }

    override suspend fun createStory(story: Story): Result<Story> {
        return Result.Error(NotImplementedError("createStory not implemented yet"))
    }

    override suspend fun updateStory(story: Story): Result<Story> {
        return Result.Error(NotImplementedError("updateStory not implemented yet"))
    }

    // Story Nodes

    override suspend fun getStoryNodes(storyId: UUID): Result<List<StoryNode>> {
        return Result.Error(NotImplementedError("getStoryNodes not implemented yet"))
    }

    override suspend fun createStoryNode(node: StoryNode): Result<StoryNode> {
        return Result.Error(NotImplementedError("createStoryNode not implemented yet"))
    }

    override suspend fun updateStoryNode(node: StoryNode): Result<StoryNode> {
        return Result.Error(NotImplementedError("updateStoryNode not implemented yet"))
    }

    // Story Progress

    override suspend fun getStoryProgress(userId: UUID, storyId: UUID): Result<StoryProgress?> {
        return Result.Error(NotImplementedError("getStoryProgress not implemented yet"))
    }

    override suspend fun updateStoryProgress(progress: StoryProgress): Result<StoryProgress> {
        return Result.Error(NotImplementedError("updateStoryProgress not implemented yet"))
    }
    
    /**
     * Serializable data class for user preferences request/response
     */
    @Serializable
    private data class UserPreferencesDto(
        @SerialName("user_id")
        val userId: String,
        @SerialName("selected_genres")
        val selectedGenres: List<String>,
        @SerialName("reading_level")
        val readingLevel: Float,
        @SerialName("theme_preference")
        val themePreference: String,
        @SerialName("voice_type")
        val voiceType: String,
        @SerialName("has_completed_onboarding")
        val hasCompletedOnboarding: Boolean
    )
    
    override suspend fun saveUserPreferences(userId: UUID, preferences: UserPreferences): Result<Unit> {
        // Ensure we have a valid session token before proceeding
        if (!ensureValidSession()) {
            return Result.Error(Exception("Not authenticated"))
        }
        
        return try {
            val response = httpClient.post("$baseUrl/rest/v1/user_preferences") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                    append("Authorization", "Bearer ${currentSession?.accessToken}")
                    append("Prefer", "resolution=merge-duplicates")
                }
                setBody(UserPreferencesDto(
                    userId = userId.toString(),
                    selectedGenres = preferences.selectedGenres,
                    readingLevel = preferences.readingLevel,
                    themePreference = preferences.themePreference,
                    voiceType = preferences.voiceType,
                    hasCompletedOnboarding = preferences.hasCompletedOnboarding
                ))
            }
            
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to save preferences: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error saving preferences: ${e.message}", e))
        }
    }
    
    override suspend fun getUserPreferences(userId: UUID): Result<UserPreferences?> {
        // Ensure we have a valid session token before proceeding
        if (!ensureValidSession()) {
            return Result.Error(Exception("Not authenticated"))
        }
        
        return try {
            val response = httpClient.get("$baseUrl/rest/v1/user_preferences") {
                headers {
                    append(HttpHeaders.ApiKey, apiKey)
                    append("X-Client-Info", "QuestPod Kotlin Client")
                    append("Authorization", "Bearer ${currentSession?.accessToken}")
                }
                url {
                    parameters.append("user_id", "eq.${userId}")
                    parameters.append("select", "*")
                }
            }
            
            if (response.status.isSuccess()) {
                val preferencesDto = response.body<List<UserPreferencesDto>>()
                if (preferencesDto.isNotEmpty()) {
                    val dto = preferencesDto.first()
                    Result.Success(UserPreferences(
                        userId = dto.userId,
                        selectedGenres = dto.selectedGenres,
                        readingLevel = dto.readingLevel,
                        themePreference = dto.themePreference,
                        voiceType = dto.voiceType,
                        hasCompletedOnboarding = dto.hasCompletedOnboarding
                    ))
                } else {
                    Result.Success(null)
                }
            } else {
                Result.Error(Exception("Failed to get preferences: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error getting preferences: ${e.message}", e))
        }
    }
}
