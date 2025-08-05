package com.questpod.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Represents user preferences for stories in the QuestPod application.
 */
@Parcelize
data class StoryPreferences(
    val genre: String,
    val ageRange: IntRange,
    val readingLevel: Int, // 1-5
    val theme: String,
    val preferredVoiceType: String
) : Parcelable {
    init {
        require(readingLevel in 1..5) { "Reading level must be between 1 and 5" }
    }

    override fun toString(): String {
        return "StoryPreferences(genre='$genre', ageRange=$ageRange, readingLevel=$readingLevel, " +
                "theme='$theme', preferredVoiceType='$preferredVoiceType')"
    }
}
