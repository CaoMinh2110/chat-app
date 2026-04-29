package com.truevibeup.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.theme.Divider
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.profile.R
import com.truevibeup.feature.profile.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    isGuestMode: Boolean? = null, // Allow overriding if passed from outside
    onOpenPurchase: () -> Unit = {},
    onShare: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onDeleteAccount: suspend () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    // Priority: parameter > viewModel state
    val finalIsGuestMode = isGuestMode ?: state.isGuestMode
    val isPremium = state.isPremium

    var showSocialDialog by remember { mutableStateOf(false) }
    var socialLoading by remember { mutableStateOf<String?>(null) }
    var deleteLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Surface
                        )
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.title_settings),
                        color = Surface,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            SectionTitle(R.string.title_general)

            SectionCard {
                SettingsRow(
                    icon = Icons.Rounded.Star,
                    label = if (isPremium) R.string.title_premium_active else R.string.title_go_premium,
                    enabled = !isPremium,
                    onClick = onOpenPurchase
                )

                SettingDivider()

                SettingsRow(
                    icon = Icons.Rounded.Share,
                    label = R.string.title_share,
                    onClick = onShare
                )

                SettingDivider()

                SettingsRow(
                    icon = Icons.Rounded.Language,
                    label = R.string.title_language,
                    onClick = onLanguage
                )
            }

            SectionTitle(R.string.title_account)

            SectionCard {

                if (finalIsGuestMode) {
                    SettingsRow(
                        icon = Icons.Rounded.Link,
                        label = R.string.title_link_social,
                        onClick = { showSocialDialog = true }
                    )
                    SettingDivider()
                }

                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.ExitToApp,
                    label = R.string.title_logout,
                    onClick = {
                        viewModel.logout {
                            navController.navigate(NavRoute.Welcome.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )

                SettingDivider()

                SettingsRow(
                    icon = Icons.Rounded.Delete,
                    label = R.string.title_delete_account,
                    destructive = true,
                    loading = deleteLoading,
                    onClick = {
                        scope.launch {
                            deleteLoading = true
                            try {
                                onDeleteAccount()
                            } finally {
                                deleteLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false
) {
    val color = if (destructive) Color.Red else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(label),
            color = color,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp))
        } else {
            Icon(Icons.Rounded.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun SectionTitle(textRes: Int) {
    Text(
        text = stringResource(textRes),
        color = TextSecondary,
        modifier = Modifier
            .padding(8.dp)
            .padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingDivider() {
    HorizontalDivider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Divider
    )
}
