package com.truevibeup.core.ui.component

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.common.model.SUPPORTED_LANGUAGES
import com.truevibeup.core.common.model.User
import com.truevibeup.core.ui.R
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Divider
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.core.ui.util.ComposeFileProvider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    user: User?,
    posts: List<Post>,
    currentUserId: String,
    isLoading: Boolean = false,
    isOwner: Boolean = true,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = true,
    isUploadingAvatar: Boolean = false,
    uploadError: String? = null,
    onUpdateAvatar: (Uri) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onFollowersClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onLoadMore: () -> Unit = {},
    onCreatePost: (String, List<Uri>) -> Unit = { _, _ -> },
    onLikePost: (Long) -> Unit = {},
    onUnlikePost: (Long) -> Unit = {},
    onDeletePost: (Long) -> Unit = {},
    onPostClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = onPostClick,
    onAuthorClick: (String) -> Unit = {},
) {
    var showAvatarEditSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showCalendarSheet by remember { mutableStateOf(false) }
    var showUserSheet by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uploadError) {
        if (uploadError != null) showErrorDialog = uploadError
    }

    var postInputValue by remember { mutableStateOf("") }
    val selectedImages = remember { mutableStateListOf<Uri>() }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val lastVisibleIndex by remember {
        derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
    }
    LaunchedEffect(lastVisibleIndex) {
        if (lastVisibleIndex >= posts.size - 3 && !isLoadingMore && hasMore) {
            onLoadMore()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris -> if (uris.isNotEmpty()) selectedImages.addAll(uris) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onUpdateAvatar(uri) }

    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) onUpdateAvatar(uri) }

    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) cameraUri.value?.let { onUpdateAvatar(it) } }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (isLoading) {
            item { ProfileShimmer(onBackClick != null) }
            items(3) { PostCardShimmer() }
        } else {
            item {
                ProfileHeader(
                    imageUrl = user?.avatar,
                    username = user?.name,
                    age = user?.age,
                    birthday = user?.birthday,
                    country = user?.country,
                    city = user?.city,
                    language = user?.language,
                    followers = user?.followersCount,
                    following = user?.followingCount,
                    isOwner = isOwner,
                    isFollowing = user?.isFollowing ?: false,
                    onEditClick = onEditClick,
                    onSettingsClick = onSettingsClick,
                    onFollowClick = onFollowClick,
                    onMessageClick = onMessageClick,
                    onFollowersClick = onFollowersClick,
                    onFollowingClick = onFollowingClick,
                    onAvatarClick = { if (isOwner) showAvatarEditSheet = true },
                    onBackClick = onBackClick,
                )
            }

            item {
                BioSection(user?.traits, user?.hobbies, user?.movies, user?.music)
            }

            if (!user?.description.isNullOrBlank()) {
                item {
                    InfoCard(title = R.string.title_bio) {
                        Text(
                            text = user!!.description!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                        )
                    }
                }
            }

            item {
                LookingForSection(
                    user?.lookingForAgeMin,
                    user?.lookingForAgeMax,
                    user?.lookingForGender,
                    user?.lookingForPersonality
                )
            }

            if (user != null && !user.photos.isNullOrEmpty()) {
                item {
                    InfoCard(title = R.string.title_photos) {
                        PhotoGallery(user.photos!!)
                    }
                }
            }

            if (isOwner) {
                item {
                    InfoCard {
                        PostInput(
                            imageUrl = user?.avatar,
                            value = postInputValue,
                            photoUrls = selectedImages.map { it.toString() },
                            onAddClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            onValueChange = { postInputValue = it },
                            onRemovePhoto = { index -> selectedImages.removeAt(index) },
                            onPostClick = {
                                if (postInputValue.isNotBlank() || selectedImages.isNotEmpty()) {
                                    onCreatePost(postInputValue, selectedImages.toList())
                                    postInputValue = ""
                                    selectedImages.clear()
                                }
                            }
                        )
                    }
                }
            }

            item {
                InfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.title_posts).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        BadgedIcon(
                            icon = IconResource.Vector(Icons.Rounded.FilterAlt),
                            size = 30.dp,
                            iconColor = Color.Black.copy(alpha = 0.7f),
                            backgroundColor = Color.White,
                            number = 2
                        ) { showFilterSheet = true }
                    }
                }
            }

            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    currentUserId = currentUserId,
                    onLike = { onLikePost(post.id) },
                    onUnlike = { onUnlikePost(post.id) },
                    onFollow = {},
                    onUnfollow = {},
                    onAuthorClick = { onAuthorClick(it) },
                    onPostClick = { onPostClick(it) },
                    onCommentClick = { onCommentClick(it) },
                    onDelete = { onDeletePost(it) }
                )
            }

            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }

    if (isOwner && showAvatarEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarEditSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                FilterRow(
                    icon = Icons.Rounded.CameraAlt,
                    label = stringResource(R.string.title_take_photo),
                    onClick = {
                        showAvatarEditSheet = false
                        val uri = ComposeFileProvider.getImageUri(context)
                        cameraUri.value = uri
                        cameraLauncher.launch(uri)
                    }
                )
                FilterRow(
                    icon = Icons.Rounded.PhotoLibrary,
                    label = stringResource(R.string.title_photo_gallery),
                    onClick = {
                        showAvatarEditSheet = false
                        try {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } catch (e: Exception) {
                            legacyGalleryLauncher.launch("image/*")
                        }
                    }
                )
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                FilterRow(
                    icon = Icons.Rounded.CalendarMonth,
                    label = stringResource(R.string.title_post),
                    value = "Hiện tại",
                    onClick = { showFilterSheet = false; showCalendarSheet = true }
                )
                FilterRow(
                    icon = Icons.Rounded.Category,
                    label = stringResource(R.string.title_author),
                    value = "Tất cả",
                    onClick = { showFilterSheet = false; showUserSheet = true }
                )
            }
        }
    }

    if (showCalendarSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCalendarSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }

    if (showUserSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUserSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }

    if (isOwner && isUploadingAvatar) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.message_upload_image)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(stringResource(R.string.message_wait))
                }
            },
            confirmButton = {}
        )
    }

    showErrorDialog?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = null }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun ProfileShimmer(hasBack: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        Row {
            if (hasBack) {
                ShimmerBox(width = 36.dp, height = 36.dp, cornerRadius = 18.dp)
            }
            Spacer(Modifier.weight(1f))
            ShimmerBox(width = 36.dp, height = 36.dp, cornerRadius = 18.dp)
            Spacer(Modifier.width(8.dp))
            ShimmerBox(width = 36.dp, height = 36.dp, cornerRadius = 18.dp)
        }
        Spacer(Modifier.height(12.dp))
        ShimmerBox(width = 120.dp, height = 120.dp, cornerRadius = 60.dp)
        Spacer(Modifier.height(16.dp))
        ShimmerBox(width = 200.dp, height = 28.dp)
        Spacer(Modifier.height(16.dp))
        ShimmerBox(width = 150.dp, height = 14.dp)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerBox(width = 80.dp, height = 16.dp)
            ShimmerBox(width = 80.dp, height = 16.dp)
        }
        Spacer(Modifier.height(16.dp))
        ShimmerBox(height = 40.dp, cornerRadius = 12.dp)
    }
}

@Composable
fun FilterRow(
    icon: ImageVector,
    label: String,
    value: String = "",
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = value.isBlank(), onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        if (value.isNotBlank()) {
            TextButton(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Background)
            ) {
                Text(text = value, color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            title?.let {
                Text(
                    text = stringResource(it).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            content()
        }
    }
}

@Composable
private fun ProfileHeader(
    imageUrl: String? = null,
    username: String? = null,
    age: Int? = null,
    birthday: String? = null,
    country: String? = null,
    city: String? = null,
    language: String? = null,
    followers: Int? = null,
    following: Int? = null,
    isOwner: Boolean = true,
    isFollowing: Boolean = false,
    onEditClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onFollowersClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        Row {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                }
                Spacer(Modifier.width(4.dp))
            }

            Spacer(Modifier.weight(1f))

            if (isOwner) {
                IconButton(onClick = {}) {
                    GetIcon(icon = IconResource.Vector(Icons.Default.Diamond), iconColor = Primary)
                }
                IconButton(onClick = onSettingsClick) {
                    GetIcon(icon = IconResource.Vector(Icons.Rounded.Settings), iconColor = Primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Avatar(
            imageUrl = imageUrl,
            size = 120.dp,
            isEditable = isOwner,
            onEditClick = onAvatarClick,
            onImageClick = onAvatarClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = listOfNotNull(
                username,
                age?.let { "$it ${stringResource(R.string.prefix_age)}" }
            ).joinToString(" · "),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = listOfNotNull(birthday, city, country).joinToString(" · "),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        language?.let {
            Text(
                text = SUPPORTED_LANGUAGES
                    .filter { language -> language.code == it }
                    .getOrElse(0) { index -> SUPPORTED_LANGUAGES[index] }.name,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row {
            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFollowersClick
                ),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(followers?.toString() ?: "0", fontWeight = FontWeight.Bold)
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.prefix_followers),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFollowingClick
                ),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(following?.toString() ?: "0", fontWeight = FontWeight.Bold)
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.prefix_following),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (isOwner) {
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(stringResource(R.string.title_edit_profile), color = Color.White)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) Color.LightGray else Primary
                    )
                ) {
                    Icon(
                        if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(if (isFollowing) R.string.title_following else R.string.title_follow),
                        color = Color.White
                    )
                }
                OutlinedButton(
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Rounded.ChatBubble,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.title_message))
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun BioSection(
    traits: List<String>?,
    hobbies: List<String>?,
    movies: List<String>?,
    music: List<String>?,
) {
    val items = mutableListOf<Pair<Int, String>>()
    traits?.takeIf { it.isNotEmpty() }
        ?.let { items.add(R.string.title_traits to it.sorted().joinToString(", ")) }
    hobbies?.takeIf { it.isNotEmpty() }
        ?.let { items.add(R.string.title_interests to it.sorted().joinToString(", ")) }
    movies?.takeIf { it.isNotEmpty() }
        ?.let { items.add(R.string.title_movies to it.sorted().joinToString(", ")) }
    music?.takeIf { it.isNotEmpty() }
        ?.let { items.add(R.string.title_music to it.sorted().joinToString(", ")) }

    if (items.isNotEmpty()) {
        InfoCard(title = R.string.title_about_me) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val headerWidth = maxWidth / 4
                Column(Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        GridCells(
                            item.first,
                            item.second,
                            headerWidth,
                            index != items.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun LookingForSection(
    lookingForAgeMin: Int?,
    lookingForAgeMax: Int?,
    lookingForGender: String?,
    lookingForPersonality: String?,
) {
    val items = mutableListOf<Pair<Int, String>>()
    lookingForAgeMin?.let { min -> lookingForAgeMax?.let { max -> items.add(R.string.title_looking_for_ages to "$min - $max") } }
    lookingForGender?.let { items.add(R.string.title_looking_for_gender to it) }
    lookingForPersonality?.let { items.add(R.string.title_looking_for_personality to it) }

    if (items.isNotEmpty()) {
        InfoCard(title = R.string.title_looking_for) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val headerWidth = maxWidth / 3
                Column(Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        GridCells(
                            item.first,
                            item.second,
                            headerWidth,
                            index != items.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridCells(titleRes: Int, value: String, headerWidth: Dp, showDivider: Boolean = true) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(headerWidth),
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
        )
        Box(Modifier.fillMaxSize()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            if (showDivider) HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                color = Divider
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhotoGallery(photoUrls: List<String>, maxVisible: Int = 8, maxItemsEachRow: Int = 4) {
    val spacing = 8.dp
    BoxWithConstraints {
        val itemSize = (maxWidth - spacing * (maxItemsEachRow - 1)) / maxItemsEachRow - 1.dp
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = maxItemsEachRow,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            val visiblePhotos = photoUrls.take(maxVisible)
            visiblePhotos.forEachIndexed { index, url ->
                Box(modifier = Modifier.size(itemSize)) {
                    Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(8.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (index == visiblePhotos.lastIndex && photoUrls.size > maxVisible) {
                        val remaining = photoUrls.size - maxVisible
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$remaining", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PostInput(
    imageUrl: String?,
    value: String,
    maxItemsEachRow: Int = 5,
    photoUrls: List<String> = emptyList(),
    onAddClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onRemovePhoto: (Int) -> Unit,
    onPostClick: () -> Unit = {},
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Avatar(imageUrl = imageUrl, borderThickness = 1.dp, size = 40.dp)
        TextField(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_content_input),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        if (value.isNotBlank()) {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = Primary
                )
            }
        }
    }
    HorizontalDivider(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    if (photoUrls.isNotEmpty()) {
        val spacing = 6.dp
        BoxWithConstraints {
            val itemSize = (maxWidth - spacing * (maxItemsEachRow - 1)) / maxItemsEachRow - 1.dp
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxItemsInEachRow = maxItemsEachRow,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                photoUrls.forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .size(itemSize)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.White.copy(alpha = 0.25f))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onRemovePhoto(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                if (photoUrls.size < 10) {
                    Box(modifier = Modifier.size(itemSize)) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .dashedBorder(
                                    color = Primary,
                                    strokeWidth = 2.dp,
                                    cornerRadius = 6.dp
                                )
                                .clickable { onAddClick() },
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "+",
                                    fontSize = 20.sp,
                                    color = Primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (photoUrls.isEmpty()) {
            Row(
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddClick
                ),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Rounded.Image, contentDescription = null, tint = Primary)
                Text(text = stringResource(R.string.title_image), color = Primary)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onPostClick,
            enabled = value.isNotBlank() || photoUrls.isNotEmpty(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                disabledContainerColor = Primary.copy(alpha = 0.5f)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(text = stringResource(R.string.title_post), color = Color.White)
            }
        }
    }
}
