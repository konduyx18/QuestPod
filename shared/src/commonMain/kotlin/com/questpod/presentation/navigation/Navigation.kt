package com.questpod.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * Sealed class representing all navigation destinations in the QuestPod app.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Auth screens
    object Onboarding : Screen("onboarding", "Onboarding")
    object Login : Screen("login", "Login")
    
    // Main screens with bottom navigation
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Discover : Screen("discover", "Discover", Icons.Filled.Explore)
    object StoryCreator : Screen("story_creator", "Create", Icons.Filled.Create)
    object Profile : Screen("profile", "Profile", Icons.Filled.AccountCircle)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    
    // Detail screens
    object StoryPlayer : Screen("story_player/{storyId}", "Story") {
        fun createRoute(storyId: String) = "story_player/$storyId"
        
        // Deep link URI for sharing stories
        val deepLinkUri = "https://questpod.app/story/{storyId}"
    }
    
    // List of screens that appear in the bottom navigation
    companion object {
        val BottomNavItems = listOf(Home, Discover, StoryCreator, Profile)
    }
}

/**
 * Navigation actions as extension functions for NavController.
 */
class NavigationActions(private val navController: NavController) {
    
    /**
     * Navigate to a destination while managing the back stack.
     */
    fun navigateSingleTop(route: String) {
        navController.navigate(route) {
            // Pop up to the start destination of the graph to avoid building up a large stack
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    
    /**
     * Navigate to the home screen.
     */
    fun navigateToHome() = navigateSingleTop(Screen.Home.route)
    
    /**
     * Navigate to the discover screen.
     */
    fun navigateToDiscover() = navigateSingleTop(Screen.Discover.route)
    
    /**
     * Navigate to the story creator screen.
     */
    fun navigateToStoryCreator() = navigateSingleTop(Screen.StoryCreator.route)
    
    /**
     * Navigate to the profile screen.
     */
    fun navigateToProfile() = navigateSingleTop(Screen.Profile.route)
    
    /**
     * Navigate to the settings screen.
     */
    fun navigateToSettings() = navigateSingleTop(Screen.Settings.route)
    
    /**
     * Navigate to the story player screen.
     */
    fun navigateToStoryPlayer(storyId: String) = 
        navController.navigate(Screen.StoryPlayer.createRoute(storyId))
    
    /**
     * Navigate to the login screen, clearing the back stack.
     */
    fun navigateToLogin() {
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
    
    /**
     * Navigate to the onboarding screen, clearing the back stack.
     */
    fun navigateToOnboarding() {
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
}

/**
 * Bottom navigation bar composable for the main screens of the QuestPod app.
 *
 * @param navController The NavController for handling navigation events.
 * @param notificationCounts Map of routes to notification counts for badges.
 */
@Composable
fun QuestPodBottomNavigation(
    navController: NavController,
    notificationCounts: Map<String, Int> = emptyMap()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navigationActions = NavigationActions(navController)
    
    NavigationBar {
        Screen.BottomNavItems.forEach { screen ->
            val hasNotifications = (notificationCounts[screen.route] ?: 0) > 0
            val notificationCount = notificationCounts[screen.route] ?: 0
            
            NavigationBarItem(
                icon = {
                    screen.icon?.let { icon ->
                        if (hasNotifications) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(text = notificationCount.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = screen.title
                            )
                        }
                    }
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        when (screen) {
                            Screen.Home -> navigationActions.navigateToHome()
                            Screen.Discover -> navigationActions.navigateToDiscover()
                            Screen.StoryCreator -> navigationActions.navigateToStoryCreator()
                            Screen.Profile -> navigationActions.navigateToProfile()
                            else -> {} // Should not happen for bottom nav items
                        }
                    }
                }
            )
        }
    }
}

/**
 * Determines whether the bottom navigation should be shown for the current route.
 *
 * @param currentRoute The current navigation route.
 * @return True if the bottom navigation should be shown, false otherwise.
 */
fun shouldShowBottomNav(currentRoute: String?): Boolean {
    return Screen.BottomNavItems.any { it.route == currentRoute }
}

/**
 * Creates deep link for a story to enable sharing.
 *
 * @param storyId The ID of the story to share.
 * @return The deep link URI as a string.
 */
fun createStoryDeepLink(storyId: String): String {
    return Screen.StoryPlayer.deepLinkUri.replace("{storyId}", storyId)
}

/**
 * Creates the NavDeepLink for the story player.
 *
 * @return The NavDeepLink object for the story player.
 */
fun createStoryDeepLinkNavArg(): List<NavDeepLink> {
    return listOf(navDeepLink { uriPattern = Screen.StoryPlayer.deepLinkUri })
}

/**
 * Creates the navigation arguments for the story player.
 *
 * @return The list of navigation arguments for the story player.
 */
fun createStoryNavArguments(): List<navArgument> {
    return listOf(
        navArgument("storyId") {
            type = NavType.StringType
            nullable = false
        }
    )
}
