package com.questpod.domain.usecase

import com.questpod.data.repository.StoryRepository
import com.questpod.domain.model.Story
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving public stories.
 */
class GetPublicStoriesUseCase(private val storyRepository: StoryRepository) {
    /**
     * Execute the use case to get all public stories.
     */
    suspend operator fun invoke(): Flow<List<Story>> {
        return storyRepository.getPublicStories()
    }
}
