package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.feature.auth.R
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel


@Composable
fun SplashScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoading, state.isAuthenticated) {
        if (!state.isLoading) {
            if (state.isAuthenticated) {
                navController.navigate(NavRoute.Main.route) {
                    popUpTo(NavRoute.Splash.route) { inclusive = true }
                }
            } else {
                navController.navigate(NavRoute.Welcome.route) {
                    popUpTo(NavRoute.Splash.route) { inclusive = true }
                }
            }
        }
    }

    SplashScreenContent()
}

@Preview
@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.Center,
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.splashscreen_logo),
                contentDescription = null
            )
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp), color = Surface,
                strokeCap = StrokeCap.Round,
                strokeWidth = 5.dp
            )
        }
    }
}
