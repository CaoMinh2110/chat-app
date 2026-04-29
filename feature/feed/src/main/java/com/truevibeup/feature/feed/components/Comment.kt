package com.truevibeup.feature.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.truevibeup.core.common.AppConstants.MAX_COMMENT_LEVEL
import com.truevibeup.core.storage.SecureStorage
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.theme.BackgroundAlt
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.util.formatTime
import com.truevibeup.feature.feed.R
import com.truevibeup.feature.feed.viewmodel.FlatComment
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItemView(
    comment: FlatComment,
    replyingToCommentId: Long? = null,
    onReplyClick: (Long) -> Unit = {},
    onToggleReplies: (Long) -> Unit = {},
    onImageClick: (String) -> Unit = {},
) {
    val isBeingRepliedTo = comment.id == replyingToCommentId
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    
    var translatedText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isBeingRepliedTo) {
        if (isBeingRepliedTo) {
            delay(300)
            bringIntoViewRequester.bringIntoView()
        }
    }

    val avatarSize = 36.dp
    val avatarPadding = 10.dp
    val indentWidth = avatarSize + avatarPadding

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .background(if (isBeingRepliedTo) BackgroundAlt else Color.Transparent)
            .padding(horizontal = 16.dp)
    ) {
        CommentHeaderRow(
            comment = comment,
            translatedText = translatedText,
            avatarSize = avatarSize,
            avatarPadding = avatarPadding,
            indentWidth = indentWidth,
            showTreeLine = !comment.isLastSibling,
            onImageClick = onImageClick,
        )

        if (comment.depth < MAX_COMMENT_LEVEL - 1) {
            CommentActionRow(
                comment = comment,
                isTranslated = translatedText != null,
                onTranslateResult = { translatedText = it },
                onToggleReplies = { onToggleReplies(comment.id) },
                onReplyClick = { onReplyClick(comment.id) },
            )
        }
    }
}

@Composable
private fun CommentHeaderRow(
    comment: FlatComment,
    translatedText: String?,
    avatarSize: Dp,
    avatarPadding: Dp,
    indentWidth: Dp,
    showTreeLine: Boolean,
    onImageClick: (String) -> Unit,
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Row {
            repeat((comment.depth - 1).coerceAtLeast(0)) {
                Spacer(Modifier.width(indentWidth))
            }

            if (comment.depth > 0) {
                Box(
                    modifier = Modifier
                        .width(avatarSize)
                        .fillMaxHeight()
                        .drawBehind {
                            drawCommentTree(
                                avatarSize = avatarSize,
                                showTreeLine = showTreeLine,
                                avatarPadding = avatarPadding.toPx()
                            )
                        }
                )
                Spacer(Modifier.width(avatarPadding))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 10.dp),
        ) {
            Avatar(
                imageUrl = comment.author.avatar,
                size = avatarSize,
                onImageClick = { onImageClick(comment.author.id) }
            )

            Spacer(Modifier.height(avatarPadding))

            if (comment.replyCount > 0 && comment.isExpanded) {
                VerticalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = TextMuted
                )
            }
        }

        Spacer(Modifier.width(avatarPadding))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp, bottom = if (comment.depth == 1) 4.dp else 0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = " · ${formatTime(comment.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = translatedText ?: comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CommentActionRow(
    comment: FlatComment,
    isTranslated: Boolean,
    onTranslateResult: (String?) -> Unit,
    onToggleReplies: () -> Unit,
    onReplyClick: () -> Unit,
) {
    val context = LocalContext.current
    val secureStorage = remember { 
        val entryPoint = dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            TranslationEntryPoint::class.java
        )
        entryPoint.secureStorage()
    }

    var showTranslateButton by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var userLanguage by remember { mutableStateOf("en") }

    LaunchedEffect(comment.content) {
        userLanguage = secureStorage.getLanguage() ?: "en"
        val languageIdentifier = LanguageIdentification.getClient()
        try {
            val languageCode = languageIdentifier.identifyLanguage(comment.content).await()
            if (languageCode != "und" && languageCode != userLanguage) {
                showTranslateButton = true
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (comment.replyCount > 0 && comment.isExpanded) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 1.dp,
                    color = TextMuted
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier
            .weight(1f)
            .padding(bottom = 4.dp)) {
            Spacer(Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = showTranslateButton) {
                    Text(
                        text =  stringResource(
                            if (isTranslated)R.string.title_show_original
                            else R.string.title_translate
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                if (isTranslated) {
                                    onTranslateResult(null)
                                } else {
                                    isTranslating = true
                                    translateText(comment.content, userLanguage) { result ->
                                        onTranslateResult(result)
                                        isTranslating = false
                                    }
                                }
                            }
                        ),
                    )
                }

                if (comment.replyCount > 0) {
                    Row(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onToggleReplies
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            if (comment.isExpanded) Icons.Rounded.KeyboardArrowUp
                            else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (comment.isExpanded)
                                stringResource(R.string.title_hide_replies)
                            else
                                stringResource(R.string.prefix_show_replies, comment.replyCount),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextMuted
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.title_reply),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMuted,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onReplyClick
                    )
                )
            }
        }
    }
}

private fun translateText(text: String, targetLang: String, onResult: (String) -> Unit) {
    val languageIdentifier = LanguageIdentification.getClient()
    languageIdentifier.identifyLanguage(text)
        .addOnSuccessListener { sourceLang ->
            if (sourceLang == "und") return@addOnSuccessListener
            
            val sourceLangCode = TranslateLanguage.fromLanguageTag(sourceLang) ?: return@addOnSuccessListener
            val targetLangCode = TranslateLanguage.fromLanguageTag(targetLang) ?: return@addOnSuccessListener

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLangCode)
                .setTargetLanguage(targetLangCode)
                .build()
            val translator = Translation.getClient(options)
            
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            onResult(translatedText)
                        }
                }
        }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface TranslationEntryPoint {
    fun secureStorage(): SecureStorage
}

private fun DrawScope.drawCommentTree(
    avatarSize: Dp,
    showTreeLine: Boolean,
    avatarPadding: Float
) {
    val stroke = 1.dp.toPx()
    val halfAvatar = avatarSize.toPx() / 2 + avatarPadding
    val centerX = avatarSize.toPx() / 2
    val cornerRadius = minOf(16.dp.toPx(), halfAvatar)

    val path = Path().apply {
        moveTo(centerX, 0f)
        lineTo(centerX, halfAvatar - cornerRadius)
        quadraticBezierTo(
            centerX, halfAvatar,
            centerX + cornerRadius, halfAvatar
        )
        lineTo(size.width, halfAvatar)
    }

    drawPath(path, color = TextMuted, style = Stroke(width = stroke))

    if (showTreeLine) {
        drawLine(
            color = TextMuted,
            start = Offset(centerX, 0.dp.toPx()),
            end = Offset(centerX, size.height),
            strokeWidth = stroke
        )
    }
}
