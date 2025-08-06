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
 * Sealed class representing UI state for stories screen.
 */
sealed class StoriesUiState {
    /** Loading state when data is being fetched */
    object Loading : StoriesUiState()
    
    /** Success state with loaded stories */
    data class Success(val stories: List<Story>) : StoriesUiState()
    
    /** Error state with error message */
    data class Error(val message: String) : StoriesUiState()
}

/**
 * ViewModel for managing stories in the UI.
 */
class StoriesViewModel(
    private val getPublicStoriesUseCase: GetPublicStoriesUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow<StoriesUiState>(StoriesUiState.Loading)
    val uiState: StateFlow<StoriesUiState> = _uiState.asStateFlow()
    
    /**
     * Load public stories.
     */
    fun loadPublicStories() {
        coroutineScope.launch {
            try {
                _uiState.value = StoriesUiState.Loading
                
                getPublicStoriesUseCase().collect { storyList ->
                    _uiState.value = StoriesUiState.Success(storyList)
                }
            } catch (e: Exception) {
                _uiState.value = StoriesUiState.Error(e.message ?: "Failed to load stories")
            }
        }
    }
}
