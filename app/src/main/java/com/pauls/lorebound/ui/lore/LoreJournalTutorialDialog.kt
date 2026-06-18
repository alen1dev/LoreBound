package com.pauls.lorebound.ui.lore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import kotlinx.coroutines.delay

@Composable
fun LoreJournalTutorialDialog(
    onDismiss: () -> Unit
) {
    val bgAlpha = remember { Animatable(0f) }
    val symbolAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.92f) }
    val bodyAlpha = remember { Animatable(0f) }
    val body2Alpha = remember { Animatable(0f) }
    val hintAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        bgAlpha.animateTo(1f, tween(400))
        delay(200)
        symbolAlpha.animateTo(1f, tween(300))
        delay(100)
        titleAlpha.animateTo(1f, tween(500))
        titleScale.animateTo(1f, tween(500, easing = EaseOutCubic))
        delay(150)
        bodyAlpha.animateTo(1f, tween(400))
        delay(200)
        body2Alpha.animateTo(1f, tween(400))
        delay(200)
        hintAlpha.animateTo(1f, tween(300))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha.value }
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black,
                            Color.Black.copy(alpha = 0.97f),
                            BrutalAccent.copy(alpha = 0.04f),
                            Color.Black
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                }
                .padding(32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Decorative line
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = symbolAlpha.value }
                        .height(1.dp)
                        .fillMaxWidth(0.3f)
                        .background(BrutalSubtle)
                )

                Spacer(Modifier.height(24.dp))

                // Symbol
                Text(
                    text = "◈",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.W300
                    ),
                    color = BrutalAccent.copy(alpha = symbolAlpha.value)
                )

                Spacer(Modifier.height(16.dp))

                // Title
                Text(
                    text = "YOUR LORE\nJOURNAL",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.W900,
                        letterSpacing = 2.sp
                    ),
                    color = BrutalAccent.copy(alpha = titleAlpha.value),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Body text 1
                Text(
                    text = "Think of this as your journal — not just a record of quests conquered, but a place to capture your own story.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                    color = BrutalMuted.copy(alpha = bodyAlpha.value),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // Body text 2
                Text(
                    text = "Record your own expeditions. Add personal entries — a hike you took, a skill you learned, a moment worth remembering. Make it yours.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 24.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    color = Color.White.copy(alpha = body2Alpha.value * 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Decorative line
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = body2Alpha.value }
                        .height(1.dp)
                        .fillMaxWidth(0.3f)
                        .background(BrutalSubtle)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "TAP TO CONTINUE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                    color = BrutalMuted.copy(alpha = hintAlpha.value * 0.5f)
                )
            }
        }
    }
}

