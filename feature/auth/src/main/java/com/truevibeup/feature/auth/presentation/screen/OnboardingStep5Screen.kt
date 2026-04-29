package com.truevibeup.feature.auth.presentation.screen

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.truevibeup.core.common.AppConstants.MAX_PROFILE_PHOTO
import com.truevibeup.core.ui.component.dashedBorder
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.feature.auth.R
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.StepHeader

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepPhotos(photos: MutableList<String>, onNext: () -> Unit) {
    val maxPhotos = MAX_PROFILE_PHOTO
    val maxItemsEachRow = 3
    val spacing = 8.dp

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxPhotos)
    ) { uris ->
        uris.forEach { uri -> if (photos.size < maxPhotos) photos.add(uri.toString()) }
    }

    StepHeader(
        stringResource(R.string.title_add_photo),
        stringResource(R.string.message_upload_photo, maxPhotos)
    )

    Text(
        text = stringResource(R.string.prefix_max_photos, photos.size, maxPhotos),
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted
    )
    Spacer(Modifier.height(12.dp))

    BoxWithConstraints {
        val itemSize = (maxWidth - spacing * (maxItemsEachRow - 1)) / maxItemsEachRow - 1.dp
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = maxItemsEachRow,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            photos.forEachIndexed { index, url ->
                Box(modifier = Modifier.size(itemSize)) {
                    Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(8.dp)) {
                        AsyncImage(
                            model = url, contentDescription = null,
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .clickable { photos.removeAt(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            if (photos.size < maxPhotos) {
                Box(modifier = Modifier.requiredSize(itemSize)) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .dashedBorder(color = Primary, strokeWidth = 2.dp, cornerRadius = 8.dp)
                            .clickable {
                                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("+", fontSize = 24.sp, color = Primary)
                        }
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    ContinueButton(onClick = onNext)
}
