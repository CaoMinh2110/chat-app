package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.Options.HOBBIES_OPTIONS
import com.truevibeup.core.common.Options.MOVIES_OPTIONS
import com.truevibeup.core.common.Options.MUSIC_OPTIONS
import com.truevibeup.core.common.Options.TRAITS_OPTIONS
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.auth.R
import com.truevibeup.feature.auth.presentation.component.ChipMultiSelect
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.FieldLabel
import com.truevibeup.feature.auth.presentation.component.StepHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepAboutMe(
    description: String, onDescriptionChange: (String) -> Unit,
    hobbies: MutableList<String>,
    traits: MutableList<String>,
    music: MutableList<String>,
    movies: MutableList<String>,
    onNext: () -> Unit
) {
    StepHeader(stringResource(R.string.title_about_me), stringResource(R.string.message_about_me))

    FieldLabel("Bio")
    OutlinedTextField(
        value = description, onValueChange = onDescriptionChange,
        placeholder = { Text(stringResource(R.string.hint_about_you)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), maxLines = 5,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = TextSecondary, focusedBorderColor = Primary
        )
    )
    Spacer(Modifier.height(16.dp))

    ChipMultiSelect("Interests (${hobbies.size}", HOBBIES_OPTIONS, hobbies, 3)
    Spacer(Modifier.height(16.dp))
    ChipMultiSelect("Personality Traits (${traits.size}", TRAITS_OPTIONS, traits, 3)
    Spacer(Modifier.height(16.dp))
    ChipMultiSelect("Music (${music.size}", MUSIC_OPTIONS, music, 3)
    Spacer(Modifier.height(16.dp))
    ChipMultiSelect("Movies (${movies.size}", MOVIES_OPTIONS, movies, 3)
    Spacer(Modifier.height(24.dp))

    ContinueButton(onClick = onNext)
}
