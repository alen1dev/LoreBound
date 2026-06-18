package com.pauls.lorebound.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Shared star field background used for:
 * 1. Chronicle Experience screen (driven by pager scroll)
 * 2. Seasonal decoration on all tabs (Dec 15 - Jan 15, after viewing chronicle)
 *
 * @param scrollProgress Continuous value that drives star rotation.
 *   For chronicle: page + pageOffsetFraction
 *   For tabs: current tab index (animates between values)
 * @param starCount Number of stars to render
 * @param rotationPerUnit Degrees of sky rotation per unit of scrollProgress
 */
private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val depth: Float,
    val brightness: Float
)

@Composable
fun StarFieldBackground(
    modifier: Modifier = Modifier,
    scrollProgress: Float = 0f,
    starCount: Int = 80,
    rotationPerUnit: Float = 15f
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.0f + 0.5f,
                depth = Random.nextFloat(),
                brightness = Random.nextFloat() * 0.3f + 0.7f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    // Overall fade-in
    val globalAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        globalAlpha.animateTo(1f, tween(1500, delayMillis = 400))
    }

    // Twinkle
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    // Slow ambient drift
    val ambientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(120_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambientDrift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val swipeRotationDeg = scrollProgress * rotationPerUnit
        val totalRotationRad = Math.toRadians(
            (swipeRotationDeg + ambientRotation * 0.02f).toDouble()
        ).toFloat()

        stars.forEach { star ->
            val baseX = (star.x - 0.5f) * size.width
            val baseY = (star.y - 0.5f) * size.height

            val rotAmount = totalRotationRad * (0.3f + star.depth * 0.7f)
            val rotatedX = baseX * cos(rotAmount) - baseY * sin(rotAmount)
            val rotatedY = baseX * sin(rotAmount) + baseY * cos(rotAmount)

            val finalX = ((rotatedX + centerX) % size.width + size.width) % size.width
            val finalY = ((rotatedY + centerY) % size.height + size.height) % size.height

            val starAlpha = globalAlpha.value * star.brightness *
                    (0.6f + 0.4f * twinkle * (if (star.depth > 0.5f) 1f else 0.85f))

            drawCircle(
                color = Color.White.copy(alpha = starAlpha),
                radius = star.size * (0.7f + star.depth * 0.3f),
                center = Offset(finalX, finalY)
            )
        }
    }
}





