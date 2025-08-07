package com.questpod.data.network

import com.questpod.domain.model.Story
import com.questpod.domain.model.StoryNode
import com.questpod.domain.model.StoryProgress
import com.questpod.domain.model.UserPreferences
import com.questpod.domain.model.UserProfile
import java.util.UUID

/**
 * Interface for Supabase API operations including authentication and data access.
 */
interface SupabaseService {
    // Authentication
    suspend fun signInWithMagicLink(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<UserProfile?>

    // User Profiles
    suspend fun getUserProfile(userId: UUID): Result<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile>
    
    // User Preferences
    suspend fun saveUserPreferences(userId: UUID, preferences: UserPreferences): Result<Unit>
    suspend fun getUserPreferences(userId: UUID): Result<UserPreferences?>

    // Stories
    suspend fun getPublicStories(limit: Int = 20, offset: Int = 0): Result<List<Story>>
    suspend fun getFeaturedStories(limit: Int = 10): Result<List<Story>>
    suspend fun getStoriesByGenre(genre: String, limit: Int = 20, offset: Int = 0): Result<List<Story>>
    suspend fun getStoriesByUser(userId: UUID): Result<List<Story>>
    suspend fun getNewReleases(limit: Int = 10): Result<List<Story>>
    suspend fun getRecommendedStories(userId: UUID, genres: List<String>, limit: Int = 10): Result<List<Story>>
    suspend fun getStoryById(id: UUID): Result<Story?>
    suspend fun createStory(story: Story): Result<UUID>
    suspend fun updateStory(story: Story): Result<Boolean>
    suspend fun deleteStory(id: UUID): Result<Boolean>

    // Story Nodes
    suspend fun getStoryNodes(storyId: UUID): Result<List<StoryNode>>
    suspend fun createStoryNode(node: StoryNode): Result<StoryNode>
    suspend fun updateStoryNode(node: StoryNode): Result<StoryNode>

    // Story Progress
    suspend fun getStoryProgress(userId: UUID, storyId: UUID): Result<StoryProgress?>
    suspend fun updateStoryProgress(progress: StoryProgress): Result<StoryProgress>
}
