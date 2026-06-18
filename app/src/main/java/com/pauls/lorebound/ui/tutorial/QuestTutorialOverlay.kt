package com.pauls.lorebound.ui.tutorial

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.theme.DangerRed
import kotlinx.coroutines.delay

data class TutorialStep(
    val title: String,
    val description: String,
    val symbol: String,
    val accentColor: Color
)

val questTutorialSteps = listOf(
    TutorialStep(
        title = "TODAY'S QUEST",
        description = "A fresh daily quest appears here every day. Small, achievable — your everyday adventure.",
        symbol = "⚔",
        accentColor = QuestGreen
    ),
    TutorialStep(
        title = "SIDE QUEST",
        description = "Weekly challenges that push you further. These refresh each week and take a bit more commitment.",
        symbol = "◆",
        accentColor = Color(0xFF5C9EAD)
    ),
    TutorialStep(
        title = "ADVENTURE",
        description = "Monthly expeditions. Bigger, bolder quests that can reshape your character's story.",
        symbol = "◈",
        accentColor = Color(0xFFD4A54A)
    ),
    TutorialStep(
        title = "EPIC",
        description = "Rare, ambitious challenges that span months. Completing one is a true feat of legend.",
        symbol = "★",
        accentColor = Color(0xFFB55A8F)
    ),
    TutorialStep(
        title = "YOU'RE READY",
        description = "Complete quests to earn XP, grow your traits, and build your lore. Every action writes your chronicle.",
        symbol = "✦",
        accentColor = BrutalAccent
    )
)

object TutorialPreferences {
    private const val PREFS_NAME = "lorebound_tutorial"
    private const val KEY_TUTORIAL_SEEN = "quest_tutorial_seen"
    private const val KEY_LORE_TUTORIAL_SEEN = "lore_tutorial_seen"

    fun hasSeen(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_TUTORIAL_SEEN, false)
    }

    fun markSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_TUTORIAL_SEEN, true).apply()
    }

    fun reset(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_TUTORIAL_SEEN, false).apply()
    }

    fun hasSeenLore(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LORE_TUTORIAL_SEEN, false)
    }

    fun markSeenLore(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_LORE_TUTORIAL_SEEN, true).apply()
    }

    fun resetLore(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_LORE_TUTORIAL_SEEN, false).apply()
    }
}

@Composable
fun QuestTutorialOverlay(
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val step = questTutorialSteps[currentStep]
    val context = LocalContext.current

    // Staggered cinematic reveal (matching TitleUnlockDialog)
    val bgAlpha = remember { Animatable(0f) }
    val symbolAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.92f) }
    val descAlpha = remember { Animatable(0f) }
    val hintAlpha = remember { Animatable(0f) }

    LaunchedEffect(currentStep) {
        symbolAlpha.snapTo(0f)
        titleAlpha.snapTo(0f)
        titleScale.snapTo(0.92f)
        descAlpha.snapTo(0f)
        hintAlpha.snapTo(0f)

        if (currentStep == 0) {
            bgAlpha.animateTo(1f, tween(400))
            delay(150)
        } else {
            delay(80)
        }
        symbolAlpha.animateTo(1f, tween(250))
        delay(50)
        titleAlpha.animateTo(1f, tween(400))
        titleScale.animateTo(1f, tween(400, easing = EaseOutCubic))
        delay(100)
        descAlpha.animateTo(1f, tween(300))
        delay(100)
        hintAlpha.animateTo(1f, tween(250))
    }

    Dialog(
        onDismissRequest = {
            TutorialPreferences.markSeen(context)
            onDismiss()
        },
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
                            step.accentColor.copy(alpha = 0.03f),
                            Color.Black
                        )
                    )
                )
                .pointerInput(currentStep) {
                    detectTapGestures {
                        if (currentStep < questTutorialSteps.size - 1) {
                            currentStep++
                        } else {
                            TutorialPreferences.markSeen(context)
                            onDismiss()
                        }
                    }
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
                    text = step.symbol,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.W300
                    ),
                    color = step.accentColor.copy(alpha = symbolAlpha.value)
                )

                Spacer(Modifier.height(16.dp))

                // Title
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.W900,
                        letterSpacing = 2.sp
                    ),
                    color = step.accentColor.copy(alpha = titleAlpha.value),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                    }
                )

                Spacer(Modifier.height(20.dp))

                // Description
                Text(
                    text = step.description,
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

                // Progress dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.graphicsLayer { alpha = hintAlpha.value }
                ) {
                    questTutorialSteps.forEachIndexed { index, s ->
                        Surface(
                            modifier = Modifier
                                .width(if (index == currentStep) 20.dp else 6.dp)
                                .height(4.dp),
                            shape = RoundedCornerShape(2.dp),
                            color = if (index == currentStep) s.accentColor else BrutalSubtle
                        ) {}
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Hint
                Text(
                    text = if (currentStep < questTutorialSteps.size - 1)
                        "TAP TO CONTINUE" else "TAP TO BEGIN",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                    color = BrutalMuted.copy(alpha = hintAlpha.value * 0.5f)
                )
            }
        }
    }
}
