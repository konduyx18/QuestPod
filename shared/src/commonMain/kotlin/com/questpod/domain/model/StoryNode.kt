package com.questpod.domain.model

import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Represents a node in a story with content and choices.
 */
@Parcelize
data class StoryNode(
    val id: UUID,
    val content: String,
    val audioUrl: String?,
    val narratorVoiceId: String?,
    val backgroundAudioUrl: String?,
    val choices: List<Choice>,
    val educationalContent: Map<String, String>?,
    val isDeleted: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "StoryNode(id=$id, content='${content.take(30)}...', audioUrl=$audioUrl, " +
               "narratorVoiceId=$narratorVoiceId, backgroundAudioUrl=$backgroundAudioUrl, " +
               "choices=${choices.size} choices, educationalContent=$educationalContent, isDeleted=$isDeleted)"
    }
}
