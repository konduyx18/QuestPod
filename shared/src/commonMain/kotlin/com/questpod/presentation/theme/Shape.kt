package com.questpod.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Custom shape definitions for QuestPod UI components.
 * 
 * - Small: 4dp rounded corners for inputs, chips, and small elements
 * - Medium: 12dp rounded corners for cards, buttons, and medium elements
 * - Large: 16dp rounded corners for bottom sheets and large elements
 * - ExtraLarge: 24dp rounded corners for full-screen dialogs
 */
val QuestPodShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
