package com.questpod.domain.model

import java.util.UUID
import kotlinx.datetime.Instant

/**
 * Represents a user's progress in a story.
 */
data class StoryProgress(
    val userId: UUID,
    val storyId: UUID,
    val currentNodeId: UUID,
    val completedNodes: List<UUID>,
    val lastPlayedAt: Instant,
    val totalListenTime: Int
) {
    override fun toString(): String {
        return "StoryProgress(userId=$userId, storyId=$storyId, currentNodeId=$currentNodeId, " +
               "completedNodes=${completedNodes.size} nodes, lastPlayedAt=$lastPlayedAt, " +
               "totalListenTime=$totalListenTime)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StoryProgress

        if (userId != other.userId) return false
        if (storyId != other.storyId) return false
        if (currentNodeId != other.currentNodeId) return false
        if (completedNodes != other.completedNodes) return false
        if (lastPlayedAt != other.lastPlayedAt) return false
        if (totalListenTime != other.totalListenTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + storyId.hashCode()
        result = 31 * result + currentNodeId.hashCode()
        result = 31 * result + completedNodes.hashCode()
        result = 31 * result + lastPlayedAt.hashCode()
        result = 31 * result + totalListenTime
        return result
    }
}
