package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.truevibeup.core.common.model.SUPPORTED_LANGUAGES
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.PrimarySurface
import com.truevibeup.core.ui.theme.Surface

@Composable
fun SelectLanguageScreen(navController: NavController) {
    var selected by remember { mutableStateOf("en") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(top = 56.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Column {
                Text(
                    "Choose your language",
                    color = Surface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Select the language you prefer",
                    color = Surface.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SUPPORTED_LANGUAGES) { lang ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = lang.code },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected == lang.code) PrimarySurface else Surface,
                    ),
                    border = if (selected == lang.code) BorderStroke(2.dp, Primary) else null,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lang.nativeName, style = MaterialTheme.typography.titleMedium)
                            Text(lang.name, style = MaterialTheme.typography.bodySmall)
                        }
                        if (selected == lang.code) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Primary)
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("welcome") {
                    popUpTo("select_language") {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Continue", color = Surface, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
