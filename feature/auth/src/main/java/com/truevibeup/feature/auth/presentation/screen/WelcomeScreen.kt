package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.theme.FacebookBlue
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.PrimaryDark
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.feature.auth.navigation.AUTH_REGISTRATION_FLOW
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(navController: NavController) {
    val parentEntry =
        remember(navController) { navController.getBackStackEntry(AUTH_REGISTRATION_FLOW) }
    val viewModel: AuthViewModel = hiltViewModel(parentEntry)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            navController.navigate(NavRoute.Main.route) {
                popUpTo(AUTH_REGISTRATION_FLOW) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Primary, PrimaryDark))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "💝", fontSize = 80.sp)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "TrueVibeUp",
                color = Surface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Find your perfect match.\nChat. Connect. Meet.",
                color = Surface.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(60.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = Surface)
            } else {
                // Social login buttons
                Button(
                    onClick = { navController.navigate(NavRoute.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Surface),
                ) {
                    Text(
                        "Continue with Google",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { /* TODO: Facebook login */ },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FacebookBlue.copy(alpha = 0.2f),
                        disabledContainerColor = FacebookBlue,
                        disabledContentColor = Surface
                    ),
                ) {
                    Text(
                        "Continue with Facebook",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Surface.copy(alpha = 0.3f)
                    )
                    Text(
                        "  or  ",
                        color = Surface.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Surface.copy(alpha = 0.3f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { viewModel.deviceLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Surface),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Surface
                    )
                ) {
                    Text(
                        "Continue as Guest",
                        color = Surface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it, color = Surface.copy(alpha = 0.85f), fontSize = 13.sp)
            }

            Spacer(Modifier.height(32.dp))
            Text(
                text = "By continuing you agree to our Terms of Service\nand Privacy Policy",
                color = Surface.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
