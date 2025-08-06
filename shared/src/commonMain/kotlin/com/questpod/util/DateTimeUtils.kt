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
        return "${localDateTime.date} ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }
    
    /**
     * Calculate the time elapsed since a given timestamp.
     * Returns a human-readable string like "2 minutes ago", "5 hours ago", etc.
     */
    fun getTimeElapsed(timestamp: String): String {
        val instant = Instant.parse(timestamp)
        val now = Clock.System.now()
        val elapsedSeconds = now.epochSeconds - instant.epochSeconds
        
        return getLocalizedTimeElapsed(elapsedSeconds)
    }
    
    /**
     * Provides localized time elapsed strings.
     * This function can be overridden in platform-specific code to provide proper localization.
     */
    private fun getLocalizedTimeElapsed(elapsedSeconds: Long): String {
        return when {
            elapsedSeconds < 60 -> "Just now"
            elapsedSeconds < 3600 -> {
                val minutes = elapsedSeconds / 60
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            elapsedSeconds < 86400 -> {
                val hours = elapsedSeconds / 3600
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            elapsedSeconds < 604800 -> {
                val days = elapsedSeconds / 86400
                "$days ${if (days == 1L) "day" else "days"} ago"
            }
            else -> {
                val weeks = elapsedSeconds / 604800
                "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
            }
        }
    }
}
