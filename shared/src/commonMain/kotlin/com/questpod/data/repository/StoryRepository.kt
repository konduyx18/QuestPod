package com.questpod.data.repository

import com.questpod.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository interface for managing stories in the QuestPod application.
 */
interface StoryRepository {
    /**
     * Get all public stories.
     */
    suspend fun getPublicStories(): Flow<List<Story>>
    
    /**
     * Get stories created by the current user.
     */
    suspend fun getUserStories(): Flow<List<Story>>
    
    /**
     * Get a story by its ID.
     */
    suspend fun getStoryById(id: UUID): Story?
    
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
