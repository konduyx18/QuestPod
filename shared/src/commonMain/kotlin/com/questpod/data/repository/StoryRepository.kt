package com.questpod.data.repository

import com.questpod.domain.model.Story
import com.questpod.domain.model.StoryProgress
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository interface for managing stories in the QuestPod application.
 */
interface StoryRepository {
    /**
     * Get all public stories with optional pagination.
     */
    suspend fun getPublicStories(limit: Int = 20, offset: Int = 0): Flow<List<Story>>
    
    /**
     * Get featured/popular stories.
     */
    suspend fun getFeaturedStories(limit: Int = 10): Flow<List<Story>>
    
    /**
     * Get stories by genre.
     */
    suspend fun getStoriesByGenre(genre: String, limit: Int = 20, offset: Int = 0): Flow<List<Story>>
    
    /**
     * Get stories created by a specific user.
     */
    suspend fun getStoriesByUser(userId: UUID): Flow<List<Story>>
    
    /**
     * Get stories that are in progress for a user.
     */
    suspend fun getInProgressStories(userId: UUID): Flow<List<StoryProgress>>
    
    /**
     * Get new releases (recently added stories).
     */
    suspend fun getNewReleases(limit: Int = 10): Flow<List<Story>>
    
    /**
     * Get personalized recommendations for a user based on preferences.
     */
    suspend fun getRecommendedStories(userId: UUID, limit: Int = 10): Flow<List<Story>>
    
    /**
     * Get stories created by the current user.
     */
    suspend fun getUserStories(): Flow<List<Story>>
    
    /**
     * Get a story by its ID.
     */
    suspend fun getStoryById(id: UUID): Flow<Story?>
    
    /**
     * Create a new story.
     */
    suspend fun createStory(story: Story): UUID
    
    /**
     * Update an existing story.
     */
    suspend fun updateStory(story: Story): Boolean
    
    /**
     * Delete a story (soft delete).
     */
    suspend fun deleteStory(id: UUID): Boolean
}
