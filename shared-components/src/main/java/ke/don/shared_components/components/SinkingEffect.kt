package ke.don.shared_components.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

@Composable
fun SinkingBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shape: Shape,
    idleElevation: Dp,
    pressedElevation: Dp,
    pressedScale: Float,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .shadow(if (isPressed) pressedElevation else idleElevation, shape)
            .background(backgroundColor, shape)
            .scale(if (isPressed) pressedScale else 1f)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        content()
    }
}

@Composable
fun SubtleSinkingBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressedScale: Float = 0.92f,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Log press state and click events
    println("SubtleSinkingBox: isPressed = $isPressed")

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    println("SubtleSinkingBox: Clicked!")
                    onClick()
                }
            )
            .graphicsLayer {
                scaleX = if (isPressed) pressedScale else 1f
                scaleY = if (isPressed) pressedScale else 1f
            }
    ) {
        content()
    }
}