package com.truevibeup.core.common.util

import androidx.core.text.HtmlCompat

object HtmlDecoder {
    fun decode(input: String?): String? {
        if (input.isNullOrEmpty()) return input
        // Handles &#039;, &amp;, &quot;, &lt;, &gt; etc.
        return try {
            HtmlCompat.fromHtml(input, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            input
        }
    }
}
