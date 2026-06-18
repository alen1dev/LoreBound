package com.pauls.lorebound.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import kotlinx.coroutines.delay

@Composable
fun TitleUnlockDialog(
    title: Title,
    onDismiss: () -> Unit
) {
    // Staggered cinematic reveal
    val bgAlpha = remember { Animatable(0f) }
    val labelAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.92f) }
    val descAlpha = remember { Animatable(0f) }
    val hintAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        bgAlpha.animateTo(1f, tween(600))
        delay(300)
        labelAlpha.animateTo(1f, tween(500))
        delay(200)
        titleAlpha.animateTo(1f, tween(700))
        titleScale.animateTo(1f, tween(800, easing = EaseOutCubic))
        delay(300)
        descAlpha.animateTo(1f, tween(500))
        delay(400)
        hintAlpha.animateTo(1f, tween(400))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
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
                            BrutalAccent.copy(alpha = 0.03f),
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
            // Slowly rotating star field behind content
            StarFieldBackground(
                starCount = 50,
                scrollProgress = 0f,
                modifier = Modifier.graphicsLayer { alpha = bgAlpha.value }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Decorative line
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = labelAlpha.value }
                        .height(1.dp)
                        .fillMaxWidth(0.3f)
                        .background(BrutalSubtle)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "TITLE EARNED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.W300
                    ),
                    color = BrutalMuted.copy(alpha = labelAlpha.value)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "« ${title.name.uppercase()} »",
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

                Spacer(Modifier.height(20.dp))

                Text(
                    text = title.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = BrutalMuted.copy(alpha = descAlpha.value),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Decorative line
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = descAlpha.value }
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
