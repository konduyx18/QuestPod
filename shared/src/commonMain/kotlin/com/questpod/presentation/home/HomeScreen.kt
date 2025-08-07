package com.questpod.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.questpod.domain.model.Story
import com.questpod.domain.model.StoryProgress
import com.questpod.domain.model.UserProfile
import com.questpod.presentation.components.StoryCard
import com.questpod.presentation.components.StoryCardLoadingPlaceholder
import com.questpod.presentation.components.StoryCardSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Data class representing the state of the HomeScreen
 */
data class HomeScreenState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val userProfile: UserProfile? = null,
    val inProgressStories: List<Pair<Story, StoryProgress>> = emptyList(),
    val popularStories: List<Story> = emptyList(),
    val recommendedStories: List<Story> = emptyList(),
    val newReleases: List<Story> = emptyList(),
    val error: String? = null
)

/**
 * Main Home Screen that displays personalized content for the user
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onRefresh: () -> Unit = {},
    onStoryClick: (UUID) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = onRefresh
    )
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text("QuestPod")
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            if (state.isLoading) {
                // Loading state
                LoadingContent()
            } else if (state.error != null) {
                // Error state
                ErrorContent(
                    error = state.error,
                    onRetry = onRefresh
                )
            } else {
                // Content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Personalized greeting
                    item {
                        PersonalizedGreeting(userProfile = state.userProfile)
                    }
                    
                    // Continue Listening section (only if there are in-progress stories)
                    if (state.inProgressStories.isNotEmpty()) {
                        item {
                            SectionTitle(title = "Continue Listening")
                        }
                        
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.inProgressStories) { (story, progress) ->
                                    StoryCard(
                                        story = story,
                                        size = StoryCardSize.MEDIUM,
                                        completionPercentage = progress.completionPercentage,
                                        onClick = { onStoryClick(story.id) }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Popular Stories section
                    item {
                        SectionTitle(title = "Popular Stories")
                    }
                    
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.popularStories) { story ->
                                StoryCard(
                                    story = story,
                                    size = StoryCardSize.SMALL,
                                    isPremium = story.isPremium,
                                    onClick = { onStoryClick(story.id) }
                                )
                            }
                        }
                    }
                    
                    // For You (Recommended) section
                    item {
                        SectionTitle(title = "For You")
                    }
                    
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.recommendedStories) { story ->
                                StoryCard(
                                    story = story,
                                    size = StoryCardSize.MEDIUM,
                                    isPremium = story.isPremium,
                                    onClick = { onStoryClick(story.id) }
                                )
                            }
                        }
                    }
                    
                    // New Releases section
                    item {
                        SectionTitle(title = "New Releases")
                    }
                    
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.newReleases) { story ->
                                StoryCard(
                                    story = story,
                                    size = StoryCardSize.SMALL,
                                    isPremium = story.isPremium,
                                    onClick = { onStoryClick(story.id) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Pull to refresh indicator
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PersonalizedGreeting(userProfile: UserProfile?) {
    val greeting = remember {
        val hour = java.time.LocalTime.now().hour
        when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$greeting${userProfile?.let { ", ${it.displayName}" } ?: ""}!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "What would you like to explore today?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun LoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Placeholder for greeting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .height(32.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {}
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier
                        .width(250.dp)
                        .height(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {}
            }
        }
        
        // Continue Listening section placeholder
        item {
            SectionTitle(title = "Continue Listening")
        }
        
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(3) {
                    StoryCardLoadingPlaceholder(size = StoryCardSize.MEDIUM)
                }
            }
        }
        
        // Popular Stories section placeholder
        item {
            SectionTitle(title = "Popular Stories")
        }
        
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(4) {
                    StoryCardLoadingPlaceholder(size = StoryCardSize.SMALL)
                }
            }
        }
        
        // For You section placeholder
        item {
            SectionTitle(title = "For You")
        }
        
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(3) {
                    StoryCardLoadingPlaceholder(size = StoryCardSize.MEDIUM)
                }
            }
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,  // Using search as a placeholder error icon
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Retry")
        }
    }
}

/**
 * View model for the HomeScreen that manages state and data loading
 */
class HomeViewModel(
    private val storyRepository: com.questpod.data.repository.StoryRepository,
    private val supabaseService: com.questpod.data.network.SupabaseService
) {
    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> = _state
    
    init {
        loadData()
    }
    
    fun loadData() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        
        // In a real implementation, we would use viewModelScope.launch and proper error handling
        // This is a simplified version for demonstration
        try {
            val currentUser = supabaseService.getCurrentUser()
            if (currentUser != null) {
                val userId = UUID.fromString(currentUser.id)
                
                // Load user profile
                val userProfileResult = supabaseService.getUserProfile(userId)
                val userProfile = (userProfileResult as? com.questpod.data.network.Result.Success)?.data
                
                // Load in-progress stories
                val inProgressStoriesFlow = storyRepository.getInProgressStories(userId)
                // In a real implementation, we would collect this flow and update state
                
                // Load popular stories
                val popularStoriesFlow = storyRepository.getFeaturedStories()
                // In a real implementation, we would collect this flow and update state
                
                // Load recommended stories
                val recommendedStoriesFlow = storyRepository.getRecommendedStories(userId)
                // In a real implementation, we would collect this flow and update state
                
                // Load new releases
                val newReleasesFlow = storyRepository.getNewReleases()
                // In a real implementation, we would collect this flow and update state
                
                // Update state with loaded data
                _state.value = HomeScreenState(
                    isLoading = false,
                    userProfile = userProfile,
                    // These would be populated from the flows in a real implementation
                    inProgressStories = emptyList(),
                    popularStories = emptyList(),
                    recommendedStories = emptyList(),
                    newReleases = emptyList()
                )
            } else {
                _state.value = HomeScreenState(
                    isLoading = false,
                    error = "User not authenticated"
                )
            }
        } catch (e: Exception) {
            _state.value = HomeScreenState(
                isLoading = false,
                error = e.message ?: "Unknown error occurred"
            )
        }
    }
    
    fun refresh() {
        _state.value = _state.value.copy(isRefreshing = true)
        // In a real implementation, we would use viewModelScope.launch
        // and call loadData() after setting isRefreshing to true
        // For now, we'll just simulate a delay
        try {
            // Simulate network delay
            Thread.sleep(1000)
            loadData()
            _state.value = _state.value.copy(isRefreshing = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isRefreshing = false,
                error = e.message ?: "Unknown error occurred"
            )
        }
    }
}

/**
 * Preview for the HomeScreen
 */
@Composable
fun HomeScreenPreview() {
    val previewState = HomeScreenState(
        isLoading = false,
        userProfile = UserProfile(
            id = UUID.randomUUID(),
            userId = "user123",
            displayName = "Alex",
            email = "alex@example.com",
            avatarUrl = null,
            bio = "I love interactive stories!"
        ),
        inProgressStories = listOf(
            Pair(
                Story(
                    id = UUID.randomUUID(),
                    title = "The Lost City",
                    description = "An adventure in a mysterious ancient city.",
                    coverImageUrl = null,
                    authorId = UUID.randomUUID(),
                    authorName = "J. Smith",
                    isPremium = false,
                    estimatedDuration = "15"
                ),
                StoryProgress(
                    id = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    storyId = UUID.randomUUID(),
                    currentNodeId = UUID.randomUUID(),
                    completionPercentage = 0.35f,
                    lastUpdated = null
                )
            )
        ),
        popularStories = listOf(
            Story(
                id = UUID.randomUUID(),
                title = "Galactic Odyssey",
                description = "A space adventure across the stars.",
                coverImageUrl = null,
                authorId = UUID.randomUUID(),
                authorName = "E. Johnson",
                isPremium = true,
                estimatedDuration = "25"
            ),
            Story(
                id = UUID.randomUUID(),
                title = "Mystery Manor",
                description = "Solve the mystery of the abandoned manor.",
                coverImageUrl = null,
                authorId = UUID.randomUUID(),
                authorName = "L. Davis",
                isPremium = false,
                estimatedDuration = "20"
            )
        ),
        recommendedStories = listOf(
            Story(
                id = UUID.randomUUID(),
                title = "Dragon's Keep",
                description = "A fantasy adventure with dragons and magic.",
                coverImageUrl = null,
                authorId = UUID.randomUUID(),
                authorName = "R. Martin",
                isPremium = false,
                estimatedDuration = "30"
            )
        ),
        newReleases = listOf(
            Story(
                id = UUID.randomUUID(),
                title = "Cyberpunk 2099",
                description = "A futuristic adventure in a dystopian city.",
                coverImageUrl = null,
                authorId = UUID.randomUUID(),
                authorName = "V. Nguyen",
                isPremium = true,
                estimatedDuration = "40"
            ),
            Story(
                id = UUID.randomUUID(),
                title = "Haunted Hospital",
                description = "A horror story set in an abandoned hospital.",
                coverImageUrl = null,
                authorId = UUID.randomUUID(),
                authorName = "S. King",
                isPremium = false,
                estimatedDuration = "35"
            )
        )
    )
    
    com.questpod.presentation.theme.QuestPodTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                state = previewState,
                onRefresh = {},
                onStoryClick = {},
                onSearchClick = {}
            )
        }
    }
}
