package com.pauls.lorebound.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import kotlin.math.cos
import kotlin.math.sin

data class TraitData(
    val trait: Trait,
    val value: Int
)

@Composable
fun RadarChart(
    traits: List<TraitData>,
    modifier: Modifier = Modifier,
    maxValue: Int = 10,
    lineColor: Color = BrutalSubtle,
    fillColor: Color = BrutalAccent.copy(alpha = 0.15f),
    strokeColor: Color = BrutalAccent,
    labelColor: Color = BrutalMuted
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = labelColor
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2.8f
        val sides = traits.size
        val angleStep = (2 * Math.PI) / sides
        val startAngle = -Math.PI / 2 // Start from top

        // Draw grid rings
        for (ring in 1..4) {
            val ringRadius = radius * ring / 4f
            val ringPath = Path()
            for (i in 0 until sides) {
                val angle = startAngle + i * angleStep
                val x = centerX + ringRadius * cos(angle).toFloat()
                val y = centerY + ringRadius * sin(angle).toFloat()
                if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
            }
            ringPath.close()
            drawPath(ringPath, lineColor.copy(alpha = 0.3f), style = Stroke(width = 0.5f))
        }

        // Draw axis lines
        for (i in 0 until sides) {
            val angle = startAngle + i * angleStep
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            drawLine(lineColor.copy(alpha = 0.3f), Offset(centerX, centerY), Offset(x, y), strokeWidth = 0.5f)
        }

        // Draw data polygon
        val dataPath = Path()
        for (i in traits.indices) {
            val value = traits[i].value.coerceIn(0, maxValue)
            val ratio = value.toFloat() / maxValue
            val angle = startAngle + i * angleStep
            val x = centerX + radius * ratio * cos(angle).toFloat()
            val y = centerY + radius * ratio * sin(angle).toFloat()
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        drawPath(dataPath, fillColor)
        drawPath(dataPath, strokeColor, style = Stroke(width = 2f))

        // Draw data points
        for (i in traits.indices) {
            val value = traits[i].value.coerceIn(0, maxValue)
            val ratio = value.toFloat() / maxValue
            val angle = startAngle + i * angleStep
            val x = centerX + radius * ratio * cos(angle).toFloat()
            val y = centerY + radius * ratio * sin(angle).toFloat()
            drawCircle(strokeColor, radius = 3f, center = Offset(x, y))
        }

        // Draw labels
        drawLabels(this, traits, centerX, centerY, radius, startAngle, angleStep, textMeasurer, labelStyle)
    }
}

private fun drawLabels(
    drawScope: DrawScope,
    traits: List<TraitData>,
    centerX: Float,
    centerY: Float,
    radius: Float,
    startAngle: Double,
    angleStep: Double,
    textMeasurer: TextMeasurer,
    style: TextStyle
) {
    for (i in traits.indices) {
        val angle = startAngle + i * angleStep
        val labelRadius = radius + 20f
        val x = centerX + labelRadius * cos(angle).toFloat()
        val y = centerY + labelRadius * sin(angle).toFloat()

        val label = traits[i].trait.displayName.take(3).uppercase()
        val measured = textMeasurer.measure(label, style)
        val offsetX = x - measured.size.width / 2f
        val offsetY = y - measured.size.height / 2f

        drawScope.drawText(
            textLayoutResult = measured,
            topLeft = Offset(offsetX, offsetY)
        )
    }
}

