package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.ui.theme.Danger
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.PrimarySurface
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextPrimary
import com.truevibeup.feature.auth.R
import com.truevibeup.feature.auth.presentation.component.StepHeader
import com.truevibeup.feature.auth.presentation.viewmodel.AuthState

@Composable
fun StepFinish(
    state: AuthState,
    onFinish: () -> Unit
) {
    StepHeader(stringResource(R.string.title_finish_set),
        stringResource(R.string.message_finish_set)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.title_profile_complete),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    it,
                    color = Danger,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))
            listOf(
                stringResource(R.string.message_basic_info),
                stringResource(R.string.message_location),
                stringResource(R.string.message_age_preferences),
                stringResource(R.string.message_photos),
                stringResource(R.string.message_bio_interests)
            ).forEach {
                Surface(
                    color = PrimarySurface, shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        it, color = Primary, fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }

    Button(
        onClick = onFinish,
        enabled = !state.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = Surface,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Let's Go 🚀", color = Surface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
