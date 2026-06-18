package com.pauls.lorebound.ui.chronicle

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pauls.lorebound.domain.chronicle.ChronicleAvailabilityState
import com.pauls.lorebound.ui.theme.*

/**
 * Small pill-shaped Chronicle button displayed beside character name.
 * Only visible in December and early January.
 */
@Composable
fun ChronicleEntryPointCard(
    state: ChronicleAvailabilityState,
    onViewChronicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ChronicleAvailabilityState.Hidden -> {
            // Show nothing
        }
        is ChronicleAvailabilityState.Preparing -> PreparingPill(modifier = modifier)
        is ChronicleAvailabilityState.Ready -> ReadyPill(
            onViewChronicle = onViewChronicle,
            modifier = modifier
        )
    }
}

// ── PREPARING (Dec 1–14): Greyed out pill, not clickable ───────────────────

@Composable
private fun PreparingPill(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrutalSurfaceVariant)
            .border(0.5.dp, BrutalSubtle, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CHRONICLE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium,
                fontSize = 9.sp
            ),
            color = BrutalMuted
        )
    }
}

// ── READY (Dec 15 → Jan 15): Eye-catching accent pill with glow ───────────

@Composable
private fun ReadyPill(
    onViewChronicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val shimmer by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        BrutalAccent.copy(alpha = glowAlpha * 0.85f),
                        BrutalAccent.copy(alpha = glowAlpha),
                        Color(0xFFB8CC5C).copy(alpha = glowAlpha * 0.9f),
                        BrutalAccent.copy(alpha = glowAlpha)
                    ),
                    startX = shimmer * 200f,
                    endX = shimmer * 200f + 400f
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        BrutalAccent.copy(alpha = 0.4f),
                        Color(0xFFB8CC5C).copy(alpha = 0.6f),
                        BrutalAccent.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onViewChronicle() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✦ CHRONICLE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
            ),
            color = Color.Black
        )
    }
}
