package com.questpod.domain.model

import java.util.UUID
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Represents a user profile in the QuestPod application.
 */
@Parcelize
data class UserProfile(
    val id: UUID,
    val displayName: String,
    val preferences: StoryPreferences,
    val subscriptionTier: String,
    val isAdmin: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "UserProfile(id=$id, displayName='$displayName', preferences=$preferences, " +
               "subscriptionTier='$subscriptionTier', isAdmin=$isAdmin)"
    }
}
