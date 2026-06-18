package com.pauls.lorebound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalSubtle

/**
 * Ember bar difficulty indicator — filled/unfilled vertical bars
 * that scale with difficulty level (1-5). Always uses accent orange.
 */
@Composable
fun DifficultyEmberBar(
    difficulty: Int,
    modifier: Modifier = Modifier,
    barWidth: Int = 4,
    barHeight: Int = 12
) {
    val level = difficulty.coerceIn(1, 5)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(level) {
            Box(
                Modifier
                    .size(width = barWidth.dp, height = barHeight.dp)
                    .background(BrutalAccent, RoundedCornerShape(1.dp))
            )
        }
        repeat(5 - level) {
            Box(
                Modifier
                    .size(width = barWidth.dp, height = barHeight.dp)
                    .background(BrutalSubtle, RoundedCornerShape(1.dp))
            )
        }
    }
}

