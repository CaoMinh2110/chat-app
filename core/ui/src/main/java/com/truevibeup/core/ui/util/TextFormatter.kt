package com.truevibeup.core.ui.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatTime(iso: String): String = try {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val date = sdf.parse(iso) ?: return iso
    val diff = (System.currentTimeMillis() - date.time) / 1000
    when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        diff < 604800 -> "${diff / 86400}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
} catch (_: Exception) {
    iso
}