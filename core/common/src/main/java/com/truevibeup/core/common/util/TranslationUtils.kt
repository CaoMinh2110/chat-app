package com.truevibeup.core.common.util

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationUtils {
    
    suspend fun identifyLanguage(text: String): String {
        val languageIdentifier = LanguageIdentification.getClient()
        return try {
            languageIdentifier.identifyLanguage(text).await()
        } catch (e: Exception) {
            "und"
        }
    }

    suspend fun translateText(
        text: String,
        sourceLangCode: String,
        targetLangCode: String
    ): String? {
        if (sourceLangCode == "und" || sourceLangCode == targetLangCode) return null
        
        val sourceLang = TranslateLanguage.fromLanguageTag(sourceLangCode) ?: return null
        val targetLang = TranslateLanguage.fromLanguageTag(targetLangCode) ?: return null

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        
        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()
        
        return try {
            translator.downloadModelIfNeeded(conditions).await()
            translator.translate(text).await()
        } catch (e: Exception) {
            null
        } finally {
            translator.close()
        }
    }
}
