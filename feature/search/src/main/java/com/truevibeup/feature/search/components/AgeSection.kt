package com.truevibeup.feature.search.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.search.R

val AGE_RANGE = 18..80
private val ITEM_HEIGHT = 48.dp
private const val HALF_COUNT = 2
private const val VISIBLE_COUNT = HALF_COUNT * 2 + 1

@Composable
@Preview
fun AgePicker(
    title: String = "Min Age",
    value: Int = 18,
    range: IntRange = AGE_RANGE,
    onApply: (Int) -> Unit = {},
    onBack: () -> Unit = {},
) {
    var current by remember { mutableIntStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeader(title = title, onBack = onBack)

        Spacer(Modifier.height(24.dp))

        NumberPicker(
            value = current,
            range = range,
        ) { current = it }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onApply(current) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                text = stringResource(R.string.prefix_apply_age, current),
                color = Surface,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Vertical drum-roll number picker with snap behaviour.
 *
 * - Shows [VISIBLE_COUNT] rows (default 5: 2 above + selected + 2 below).
 * - Snaps to the nearest item on fling/release.
 * - When [value] changes externally (e.g. auto-adjust), scrolls to the new position.
 *
 * Internal list layout:
 *   indices 0 .. HALF_COUNT-1          → leading padding (empty)
 *   indices HALF_COUNT .. HALF_COUNT+count-1 → actual values
 *   indices HALF_COUNT+count ..        → trailing padding (empty)
 *
 * Selected item = range.first + firstVisibleItemIndex
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    value: Int,
    range: IntRange = AGE_RANGE,
    onValueChange: (Int) -> Unit,
) {
    val itemCount = range.count()

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (value - range.first).coerceIn(0, itemCount - 1)
    )
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val newValue = (range.first + firstVisibleIndex).coerceIn(range)
            if (newValue != value) onValueChange(newValue)
        }
    }

    LaunchedEffect(value) {
        val targetIndex = (value - range.first).coerceIn(0, itemCount - 1)
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT * VISIBLE_COUNT),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                .background(Primary.copy(alpha = 0.10f))
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.matchParentSize(),
        ) {
            // Leading padding: allows range.first to scroll up to the center slot
            items(HALF_COUNT) {
                Box(Modifier
                    .height(ITEM_HEIGHT)
                    .fillMaxWidth())
            }

            // Actual values
            items(count = itemCount, key = { idx -> range.first + idx }) { idx ->
                val itemValue = range.first + idx
                val isSelected = idx == firstVisibleIndex
                Box(
                    modifier = Modifier
                        .height(ITEM_HEIGHT)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemValue.toString(),
                        color = if (isSelected) Primary else TextSecondary.copy(alpha = 0.45f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isSelected) 22.sp else 16.sp,
                    )
                }
            }

            // Trailing padding: allows range.last to scroll down to the center slot
            items(HALF_COUNT) {
                Box(Modifier
                    .height(ITEM_HEIGHT)
                    .width(80.dp))
            }
        }
    }
}
