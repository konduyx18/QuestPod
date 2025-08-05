package com.questpod.domain.model

import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Represents a story in the QuestPod application.
 */
@Parcelize
data class Story(
    val id: UUID,
    val title: String,
    val description: String,
    val coverImageUrl: String?,
    val creatorId: UUID,
    val isPublic: Boolean,
    val metadata: Map<String, Any>,
    val tags: List<String>,
    val isDeleted: Boolean = false,
    val nodes: List<StoryNode>
) : Parcelable {
    override fun toString(): String {
        return "Story(id=$id, title='$title', description='$description', coverImageUrl=$coverImageUrl, " +
               "creatorId=$creatorId, isPublic=$isPublic, metadata=$metadata, tags=$tags, " +
               "isDeleted=$isDeleted, nodes=${nodes.size} nodes)"
    }
}
