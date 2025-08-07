package com.questpod.domain.model

import kotlinx.serialization.Serializable

/**
 * User preferences data class for onboarding
 */
@Serializable
data class UserPreferences(
    val userId: String = "",
    val selectedGenres: List<String> = emptyList(),
    val readingLevel: Float = 0.5f,
    val themePreference: String = "system",
    val voiceType: String = "neutral",
    val hasCompletedOnboarding: Boolean = false
)
