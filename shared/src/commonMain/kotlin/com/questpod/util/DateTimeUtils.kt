package com.questpod.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Utility functions for date and time operations.
 */
object DateTimeUtils {
    /**
     * Get the current timestamp as an ISO-8601 formatted string.
     */
    fun getCurrentTimestamp(): String {
        val now = Clock.System.now()
        return now.toString()
    }
    
    /**
     * Format an ISO-8601 timestamp string to a human-readable format.
     */
    fun formatTimestamp(timestamp: String): String {
        val instant = Instant.parse(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.hour}:${localDateTime.minute}"
    }
    
    /**
     * Calculate the time elapsed since a given timestamp.
     * Returns a human-readable string like "2 minutes ago", "5 hours ago", etc.
     */
    fun getTimeElapsed(timestamp: String): String {
        val instant = Instant.parse(timestamp)
        val now = Clock.System.now()
        val elapsedSeconds = now.epochSeconds - instant.epochSeconds
        
        return when {
            elapsedSeconds < 60 -> "Just now"
            elapsedSeconds < 3600 -> "${elapsedSeconds / 60} minutes ago"
            elapsedSeconds < 86400 -> "${elapsedSeconds / 3600} hours ago"
            elapsedSeconds < 604800 -> "${elapsedSeconds / 86400} days ago"
            else -> "${elapsedSeconds / 604800} weeks ago"
        }
    }
}
