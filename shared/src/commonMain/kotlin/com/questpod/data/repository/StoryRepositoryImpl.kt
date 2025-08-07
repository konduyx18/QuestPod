package com.questpod.data.repository

import com.questpod.data.network.Result
import com.questpod.data.network.SupabaseService
import com.questpod.domain.model.Story
import com.questpod.domain.model.StoryProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * Implementation of the StoryRepository interface that uses SupabaseService for data access.
 */
class StoryRepositoryImpl(
    private val supabaseService: SupabaseService
) : StoryRepository {

    override suspend fun getPublicStories(limit: Int, offset: Int): Flow<List<Story>> = flow {
        when (val result = supabaseService.getPublicStories(limit, offset)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getFeaturedStories(limit: Int): Flow<List<Story>> = flow {
        // Featured stories are those with high ratings or marked as featured
        when (val result = supabaseService.getFeaturedStories(limit)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getStoriesByGenre(genre: String, limit: Int, offset: Int): Flow<List<Story>> = flow {
        when (val result = supabaseService.getStoriesByGenre(genre, limit, offset)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getStoriesByUser(userId: UUID): Flow<List<Story>> = flow {
        when (val result = supabaseService.getStoriesByUser(userId)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getInProgressStories(userId: UUID): Flow<List<StoryProgress>> = flow {
        when (val result = supabaseService.getStoryProgressByUser(userId)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getNewReleases(limit: Int): Flow<List<Story>> = flow {
        // New releases are stories recently added to the platform
        when (val result = supabaseService.getNewReleases(limit)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(emptyList())
            }
        }
    }

    override suspend fun getRecommendedStories(userId: UUID, limit: Int): Flow<List<Story>> = flow {
        // Get user preferences and use them to fetch recommended stories
        val userPreferencesResult = supabaseService.getUserPreferences(userId)
        
        when (userPreferencesResult) {
            is Result.Success -> {
                val preferences = userPreferencesResult.data
                if (preferences != null) {
                    // Use preferences to get personalized recommendations
                    when (val result = supabaseService.getRecommendedStories(userId, preferences.selectedGenres, limit)) {
                        is Result.Success -> emit(result.data)
                        is Result.Error -> emit(emptyList())
                    }
                } else {
                    // Fallback to featured stories if no preferences
                    when (val result = supabaseService.getFeaturedStories(limit)) {
                        is Result.Success -> emit(result.data)
                        is Result.Error -> emit(emptyList())
                    }
                }
            }
            is Result.Error -> {
                // Log error and fallback to featured stories
                when (val result = supabaseService.getFeaturedStories(limit)) {
                    is Result.Success -> emit(result.data)
                    is Result.Error -> emit(emptyList())
                }
            }
        }
    }

    override suspend fun getUserStories(): Flow<List<Story>> = flow {
        val currentUser = supabaseService.getCurrentUser()
        if (currentUser != null) {
            when (val result = supabaseService.getStoriesByUser(UUID.fromString(currentUser.id))) {
                is Result.Success -> emit(result.data)
                is Result.Error -> {
                    // Log error
                    emit(emptyList())
                }
            }
        } else {
            emit(emptyList())
        }
    }

    override suspend fun getStoryById(id: UUID): Flow<Story?> = flow {
        when (val result = supabaseService.getStoryById(id)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> {
                // Log error
                emit(null)
            }
        }
    }

    override suspend fun createStory(story: Story): UUID {
        return when (val result = supabaseService.createStory(story)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    override suspend fun updateStory(story: Story): Boolean {
        return when (val result = supabaseService.updateStory(story)) {
            is Result.Success -> true
            is Result.Error -> false
        }
    }

    override suspend fun deleteStory(id: UUID): Boolean {
        return when (val result = supabaseService.deleteStory(id)) {
            is Result.Success -> true
            is Result.Error -> false
        }
    }
}
