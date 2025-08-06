package com.questpod.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Custom elevation values for QuestPod UI components.
 * Provides consistent elevation across the application.
 */
object QuestPodElevation {
    // No elevation (flat surfaces)
    val Level0 = 0.dp
    
    // Subtle elevation for cards and slightly raised surfaces
    val Level1 = 1.dp
    
    // Standard elevation for cards, buttons in their resting state
    val Level2 = 3.dp
    
    // Medium elevation for floating action buttons, navigation drawers
    val Level3 = 6.dp
    
    // High elevation for dialogs, menus, and other temporary surfaces
    val Level4 = 8.dp
    
    // Maximum elevation for modal sheets and critical UI elements
    val Level5 = 12.dp

    // Component-specific elevations
    val Card = Level1
    val Button = Level1
    val ButtonPressed = Level0
    val Dialog = Level4
    val ModalSheet = Level5
    val Menu = Level3
    val FloatingActionButton = Level3
    val NavigationDrawer = Level2
}
