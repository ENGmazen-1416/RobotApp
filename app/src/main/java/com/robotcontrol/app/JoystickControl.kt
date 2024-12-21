package com.robotcontrol.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun JoystickControl(
    onDirectionChanged: (String) -> Unit
) {
    var stickPosition by remember { mutableStateOf(Offset.Zero) }
    val maxDistance = 100f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            stickPosition = limitDistance(offset - Offset(size.width / 2, size.height / 2), maxDistance)
                            val direction = getDirection(stickPosition)
                            onDirectionChanged(direction)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            stickPosition = limitDistance(stickPosition + dragAmount, maxDistance)
                            val direction = getDirection(stickPosition)
                            onDirectionChanged(direction)
                        },
                        onDragEnd = {
                            stickPosition = Offset.Zero
                            onDirectionChanged("stop")
                        }
                    )
                }
        ) {
            // رسم الدائرة الخارجية
            drawCircle(
                color = Color(0xFF3D3D3D),
                radius = size.minDimension / 2,
                center = center
            )

            // رسم عصا التحكم
            drawCircle(
                color = Color(0xFF00FF9D),
                radius = 30f,
                center = center + stickPosition
            )
        }
    }
}

private fun limitDistance(offset: Offset, maxDistance: Float): Offset {
    val distance = sqrt(offset.x * offset.x + offset.y * offset.y)
    return if (distance <= maxDistance) {
        offset
    } else {
        val scale = maxDistance / distance
        Offset(offset.x * scale, offset.y * scale)
    }
}

private fun getDirection(position: Offset): String {
    if (position == Offset.Zero) return "stop"
    
    val angle = atan2(position.y, position.x) * (180 / Math.PI)
    return when {
        angle > -45 && angle <= 45 -> "right"
        angle > 45 && angle <= 135 -> "backward"
        angle > 135 || angle <= -135 -> "left"
        else -> "forward"
    }
}
