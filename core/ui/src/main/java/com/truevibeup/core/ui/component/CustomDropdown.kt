package com.truevibeup.core.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

data class DropdownPosition(
    val offsetX: Int,
    val offsetY: Int,
    val maxHeight: Dp
)

@Composable
fun calculateDropdownPosition(
    coords: LayoutCoordinates,
    density: Density,
    configuration: Configuration
): DropdownPosition {
    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }

    val position = coords.positionInWindow()

    val top = position.y
    val height = coords.size.height.toFloat()
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val navBarHeightPx = WindowInsets.navigationBars.getBottom(density).toFloat()

    val centerY = top + height / 2
    val useAbove = centerY > screenHeightPx - centerY

    // maxHeight = khoảng trống thực sự, trừ system bars ở cả hai đầu
    val maxHeightPx = if (useAbove) {
        top - statusBarHeightPx
    } else {
        screenHeightPx - (top + height) - navBarHeightPx
    }

    // offset tương đối so với anchor Box
    // Popup tự cộng anchorBounds.topLeft, không dùng window-absolute coords
    val offsetY = if (useAbove) -maxHeightPx.toInt() else height.toInt()

    return DropdownPosition(
        offsetX = 0,
        offsetY = offsetY,
        maxHeight = with(density) { maxHeightPx.toDp() - 16.dp }
    )
}

@Composable
fun <T> CustomDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    anchorCoordinates: LayoutCoordinates?,
    items: List<T>,
    itemToString: (T) -> String,
    onItemClick: (T) -> Unit,
    isLoading: Boolean = false
) {
    if (!expanded || anchorCoordinates == null) return

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val position = calculateDropdownPosition(
        anchorCoordinates,
        density,
        configuration
    )

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(position.offsetX, position.offsetY),
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(with(density) { anchorCoordinates.size.width.toDp() })
                .heightIn(max = position.maxHeight)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(itemToString(item)) },
                            onClick = {
                                onItemClick(item)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
