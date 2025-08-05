package com.questpod.presentation.viewmodel

import com.questpod.domain.model.Story
import com.questpod.domain.usecase.GetPublicStoriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing stories in the UI.
 */
class StoriesViewModel(
    private val getPublicStoriesUseCase: GetPublicStoriesUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Load public stories.
     */
    fun loadPublicStories() {
        coroutineScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                getPublicStoriesUseCase().collect { storyList ->
                    _stories.value = storyList
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load stories"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
