package com.questpod.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import java.util.UUID

// Animation durations
private const val ANIMATION_DURATION = 300

/**
 * Main navigation graph for the QuestPod application.
 * Sets up the NavHost with all destinations and provides the scaffold with bottom navigation when appropriate.
 *
 * @param navController The navigation controller to use for the graph.
 * @param startDestination The starting destination route.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestPodNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Track notification counts for bottom navigation badges
    val notificationCounts = remember { mutableStateMapOf<String, Int>() }
    
    // Create navigation actions
    val navigationActions = remember(navController) { NavigationActions(navController) }
    
    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav(currentRoute)) {
                QuestPodBottomNavigation(
                    navController = navController,
                    notificationCounts = notificationCounts
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth flow with fade transitions
            authGraph(navigationActions)
            
            // Main screens with slide transitions
            mainGraph(navigationActions)
            
            // Detail screens with custom transitions
            detailGraph(navigationActions)
        }
    }
}

/**
 * Authentication flow graph.
 */
private fun NavGraphBuilder.authGraph(navigationActions: NavigationActions) {
    // Onboarding screen
    composable(
        route = Screen.Onboarding.route,
        enterTransition = { fadeEnterTransition() },
        exitTransition = { fadeExitTransition() }
    ) {
        PlaceholderScreen("Onboarding Screen")
    }
    
    // Login screen
    composable(
        route = Screen.Login.route,
        enterTransition = { fadeEnterTransition() },
        exitTransition = { fadeExitTransition() }
    ) {
        PlaceholderScreen("Login Screen")
    }
}

/**
 * Main app screens graph.
 */
private fun NavGraphBuilder.mainGraph(navigationActions: NavigationActions) {
    // Home screen
    composable(
        route = Screen.Home.route,
        enterTransition = { slideHorizontalEnterTransition() },
        exitTransition = { slideHorizontalExitTransition() }
    ) {
        PlaceholderScreen("Home Screen")
    }
    
    // Discover screen
    composable(
        route = Screen.Discover.route,
        enterTransition = { slideHorizontalEnterTransition() },
        exitTransition = { slideHorizontalExitTransition() }
    ) {
        PlaceholderScreen("Discover Screen")
    }
    
    // Story Creator screen
    composable(
        route = Screen.StoryCreator.route,
        enterTransition = { slideHorizontalEnterTransition() },
        exitTransition = { slideHorizontalExitTransition() }
    ) {
        PlaceholderScreen("Story Creator Screen")
    }
    
    // Profile screen
    composable(
        route = Screen.Profile.route,
        enterTransition = { slideHorizontalEnterTransition() },
        exitTransition = { slideHorizontalExitTransition() }
    ) {
        PlaceholderScreen("Profile Screen")
    }
    
    // Settings screen
    composable(
        route = Screen.Settings.route,
        enterTransition = { slideHorizontalEnterTransition() },
        exitTransition = { slideHorizontalExitTransition() }
    ) {
        PlaceholderScreen("Settings Screen")
    }
}

/**
 * Detail screens graph.
 */
private fun NavGraphBuilder.detailGraph(navigationActions: NavigationActions) {
    // Story Player screen with deep linking
    composable(
        route = Screen.StoryPlayer.route,
        arguments = createStoryNavArguments(),
        deepLinks = createStoryDeepLinkNavArg(),
        enterTransition = { storyPlayerEnterTransition() },
        exitTransition = { storyPlayerExitTransition() }
    ) { backStackEntry ->
        val storyId = backStackEntry.arguments?.getString("storyId")
        PlaceholderScreen("Story Player Screen: Story ID = $storyId")
        
        // Example of handling deep link
        LaunchedEffect(storyId) {
            if (backStackEntry.isDeepLink()) {
                // Handle deep link specific logic here
                // For example, analytics tracking for shared stories
            }
        }
    }
}

/**
 * Transition animations for navigation.
 */

// Fade transitions for auth flow
private fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

// Slide horizontal transitions for lateral navigation
private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideHorizontalEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideHorizontalExitTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        targetOffsetX = { fullWidth -> -fullWidth }
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

// Custom story player transitions
private fun AnimatedContentTransitionScope<NavBackStackEntry>.storyPlayerEnterTransition(): EnterTransition {
    return slideInVertically(
        animationSpec = tween(ANIMATION_DURATION),
        initialOffsetY = { fullHeight -> fullHeight / 2 }
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION / 2))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.storyPlayerExitTransition(): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(ANIMATION_DURATION),
        targetOffsetY = { fullHeight -> -fullHeight }
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION / 2))
}

/**
 * Extension function to get the current back stack entry as state.
 */
@Composable
fun NavHostController.currentBackStackEntryAsState() = 
    androidx.navigation.compose.currentBackStackEntryAsState(this)

/**
 * Check if the current navigation was triggered by a deep link.
 */
fun NavBackStackEntry.isDeepLink(): Boolean {
    return this.arguments?.getString("android-support-nav:controller:deepLinkIntent") != null
}

/**
 * Temporary placeholder screen to use until actual screen implementations are created.
 *
 * @param text The text to display on the placeholder screen.
 */
@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}
