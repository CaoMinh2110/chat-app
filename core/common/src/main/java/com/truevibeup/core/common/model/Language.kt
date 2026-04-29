package com.truevibeup.core.common.model

data class Language(val code: String, val name: String, val nativeName: String)

val SUPPORTED_LANGUAGES = listOf(
    Language("en", "English", "English"),
    Language("fr", "French", "Français"),
    Language("es", "Spanish", "Español"),
    Language("de", "German", "Deutsch"),
    Language("ar", "Arabic", "العربية"),
    Language("ja", "Japanese", "日本語"),
    Language("zh", "Chinese", "中文"),
    Language("ko", "Korean", "한국어"),
    Language("pt", "Portuguese", "Português"),
    Language("hi", "Hindi", "हिन्दी"),
    Language("id", "Indonesian", "Bahasa Indonesia"),
    Language("th", "Thai", "ไทย"),
    Language("vi", "Vietnamese", "Tiếng Việt"),
    Language("ru", "Russian", "Русский"),
    Language("tr", "Turkish", "Türkçe"),
    Language("nl", "Dutch", "Nederlands"),
)
