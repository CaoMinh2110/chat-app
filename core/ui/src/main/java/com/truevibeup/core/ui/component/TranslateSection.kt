package com.truevibeup.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.common.util.TranslationUtils
import com.truevibeup.core.storage.SecureStorage
import com.truevibeup.core.ui.R
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.SurfaceVariant2
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.theme.TextSecondary
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

private val translationCache = mutableMapOf<String, String>()

@Composable
fun TranslateSection(
    content: String,
    modifier: Modifier = Modifier
) {
    if (content.isBlank()) return

    val context = LocalContext.current
    val secureStorage = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            TranslationEntryPoint::class.java
        ).secureStorage()
    }

    val scope = rememberCoroutineScope()
    var userLanguage by remember { mutableStateOf("en") }
    var showTranslateButton by remember { mutableStateOf(false) }
    var translatedText by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }

    LaunchedEffect(content) {
        userLanguage = secureStorage.getLanguage() ?: "en"
        val sourceLang = TranslationUtils.identifyLanguage(content)
        if (sourceLang != "und" && sourceLang != userLanguage) {
            showTranslateButton = true
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (showTranslateButton) {
            Text(
                text = stringResource(
                    if (translatedText != null) R.string.title_hide_translate
                    else R.string.title_translate
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (translatedText != null) {
                            translatedText = null
                        } else {
                            val cacheKey = "${userLanguage}_$content"
                            val cached = translationCache[cacheKey]
                            if (cached != null) {
                                translatedText = cached
                            } else {
                                isTranslating = true
                                scope.launch {
                                    val source = TranslationUtils.identifyLanguage(content)
                                    val result = TranslationUtils.translateText(
                                        content,
                                        source,
                                        userLanguage
                                    )
                                    if (result != null) {
                                        translationCache[cacheKey] = result
                                        translatedText = result
                                    }
                                    isTranslating = false
                                }
                            }
                        }
                    }
                    .padding(vertical = 4.dp)
            )
        }

        AnimatedVisibility(
            visible = translatedText != null || isTranslating,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color.LightGray
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(4.dp),
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.5f)
                    ) {}
                    Spacer(Modifier.width(12.dp))

                    if (isTranslating) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ShimmerBox(height = 14.dp, width = 200.dp)
                            Spacer(Modifier.height(4.dp))
                            ShimmerBox(height = 14.dp, width = 150.dp)
                        }
                    } else {
                        Text(
                            text = translatedText ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface TranslationEntryPoint {
    fun secureStorage(): SecureStorage
}
