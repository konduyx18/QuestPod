package com.questpod.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography for QuestPod theme with custom fonts and larger story text.
 * 
 * - Nunito font for headings (display, headline, title)
 * - Roboto font for body text and labels
 * - Larger font sizes for story text to improve readability
 */
val QuestPodTypography = Typography(
    // Display styles with Nunito font
    displayLarge = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp, // Slightly larger
        lineHeight = 68.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp, // Slightly larger
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp, // Slightly larger
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles with Nunito font
    headlineLarge = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp, // Slightly larger
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp, // Slightly larger
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp, // Slightly larger
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles with Nunito font
    titleLarge = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp, // Slightly larger
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp, // Slightly larger
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp, // Slightly larger
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles with Roboto font and larger story text
    bodyLarge = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Larger for story text
        lineHeight = 28.sp, // Increased line height for readability
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Larger for story text
        lineHeight = 24.sp, // Increased line height for readability
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // Slightly larger
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles with Roboto font
    labelLarge = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp, // Slightly larger
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp, // Slightly larger
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp, // Slightly larger
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Additional typography styles specific for story content.
 * These are optimized for reading longer text passages.
 */
object StoryTypography {
    val storyTitle = TextStyle(
        fontFamily = QuestPodFont.nunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    
    val storyContent = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp, // Larger for comfortable reading
        lineHeight = 32.sp, // Increased line height for readability
        letterSpacing = 0.15.sp
    )
    
    val storyChoice = TextStyle(
        fontFamily = QuestPodFont.robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    )
}
