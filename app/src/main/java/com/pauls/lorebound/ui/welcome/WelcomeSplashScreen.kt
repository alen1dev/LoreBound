package com.pauls.lorebound.ui.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pauls.lorebound.ui.components.StarFieldBackground
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.components.RUNE_DIVIDER_WIDE
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import kotlinx.coroutines.delay

@Composable
fun WelcomeSplashScreen(
    onContinue: () -> Unit
) {
    val titleAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val runeAlpha = remember { Animatable(0f) }
    val hintAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400)
        titleAlpha.animateTo(1f, tween(1200))
        delay(300)
        taglineAlpha.animateTo(1f, tween(1000))
        delay(200)
        runeAlpha.animateTo(1f, tween(800))
        delay(500)
        hintAlpha.animateTo(1f, tween(600))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { onContinue() }
            },
        contentAlignment = Alignment.Center
    ) {
        // Starry background with continuous spin
        val infiniteTransition = rememberInfiniteTransition(label = "starSpin")
        val spinProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 24f,
            animationSpec = infiniteRepeatable(
                animation = tween(60_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spinProgress"
        )
        StarFieldBackground(
            starCount = 60,
            scrollProgress = spinProgress
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            // Rune divider top
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.graphicsLayer { alpha = runeAlpha.value }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // LOREBOUND title
            Text(
                text = "LOREBOUND",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    shadow = Shadow(
                        color = BrutalAccent.copy(alpha = 0.6f),
                        blurRadius = 16f
                    )
                ),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    scaleX = 1f + (1f - titleAlpha.value) * 0.05f
                    scaleY = 1f + (1f - titleAlpha.value) * 0.05f
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline
            Text(
                text = "Life is the quest.\nThis is your chronicle.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    lineHeight = 28.sp
                ),
                color = BrutalMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha.value }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Rune divider bottom
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.graphicsLayer { alpha = runeAlpha.value }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Tap hint
            Text(
                text = "TAP TO BEGIN",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                color = BrutalMuted.copy(alpha = 0.6f),
                modifier = Modifier.graphicsLayer { alpha = hintAlpha.value }
            )
        }
    }
}


