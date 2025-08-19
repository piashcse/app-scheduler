package com.piashcse.appscheduler.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {

    // 12-hour format with AM/PM
    private val TIME_FORMAT_12H = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Date format
    private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Full date time format with 12-hour
    private val DATE_TIME_FORMAT_12H = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    // Short date time format
    private val SHORT_DATE_TIME_FORMAT_12H = SimpleDateFormat("MM/dd hh:mm a", Locale.getDefault())

    /**
     * Format timestamp to 12-hour time string (e.g., "02:30 PM")
     */
    fun formatTime12Hour(timestamp: Long): String {
        return TIME_FORMAT_12H.format(Date(timestamp))
    }

    /**
     * Format timestamp to date string (e.g., "Dec 25, 2024")
     */
    fun formatDate(timestamp: Long): String {
        return DATE_FORMAT.format(Date(timestamp))
    }

    /**
     * Format timestamp to full date time string (e.g., "Dec 25, 2024 at 02:30 PM")
     */
    fun formatDateTime12Hour(timestamp: Long): String {
        return DATE_TIME_FORMAT_12H.format(Date(timestamp))
    }

    /**
     * Format timestamp to short date time string (e.g., "12/25 02:30 PM")
     */
    fun formatShortDateTime12Hour(timestamp: Long): String {
        return SHORT_DATE_TIME_FORMAT_12H.format(Date(timestamp))
    }

    /**
     * Get time from hour and minute in 12-hour format
     */
    fun getTimeString12Hour(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return TIME_FORMAT_12H.format(calendar.time)
    }

    /**
     * Convert 24-hour format to 12-hour format with AM/PM
     */
    fun convert24To12Hour(hour24: Int, minute: Int): Triple<Int, Int, String> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, minute)
        }

        val hour12 = calendar.get(Calendar.HOUR)
        val hour12Display = if (hour12 == 0) 12 else hour12
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        return Triple(hour12Display, minute, amPm)
    }

    /**
     * Convert 12-hour format to 24-hour format
     */
    fun convert12To24Hour(hour12: Int, minute: Int, amPm: String): Pair<Int, Int> {
        var hour24 = hour12

        if (amPm == "AM") {
            if (hour12 == 12) hour24 = 0
        } else {
            if (hour12 != 12) hour24 = hour12 + 12
        }

        return Pair(hour24, minute)
    }

    /**
     * Check if a time is in the past
     */
    fun isTimeInPast(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }

    /**
     * Get time remaining string
     */
    fun getTimeRemaining(timestamp: Long): String {
        val diff = timestamp - System.currentTimeMillis()
        if (diff <= 0) return "Overdue"

        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)

        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}