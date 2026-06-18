package com.pauls.lorebound.ui.chronicle

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pauls.lorebound.domain.model.Chronicle
import com.pauls.lorebound.domain.model.ChronicleSlide
import com.pauls.lorebound.ui.components.StarFieldBackground
import com.pauls.lorebound.ui.theme.*

/**
 * Full-screen immersive Chronicle experience.
 * Displays the AI-generated year-in-review as a series of beautiful slides.
 */
@Composable
fun ChronicleExperienceScreen(
    chronicle: Chronicle,
    onDismiss: () -> Unit
) {
    val enterAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        enterAlpha.animateTo(1f, tween(800, easing = EaseOut))
    }

    val allSlides = buildList {
        add(null) // null = title slide
        addAll(chronicle.slides)
        add(null) // second null = closing slide
    }

    val pagerState = rememberPagerState(pageCount = { allSlides.size })

    // Track continuous scroll progress for star rotation
    val scrollProgress by remember {
        derivedStateOf {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer { alpha = enterAlpha.value }
    ) {
        // Star field behind everything — rotates with page swipes
        StarFieldBackground(scrollProgress = scrollProgress)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when {
                page == 0 -> TitleSlide(chronicle)
                page == allSlides.size - 1 -> ClosingSlide(chronicle, onDismiss)
                else -> ContentSlide(chronicle.slides[page - 1])
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(allSlides.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 8.dp else 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) BrutalAccent
                            else BrutalMuted.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Close hint
        Text(
            text = "SWIPE TO CONTINUE",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = BrutalMuted.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun TitleSlide(chronicle: Chronicle) {
    val fadeIn = remember { Animatable(0f) }
    val slideUp = remember { Animatable(60f) }
    LaunchedEffect(Unit) {
        fadeIn.animateTo(1f, tween(1200, delayMillis = 300))
    }
    LaunchedEffect(Unit) {
        slideUp.animateTo(0f, tween(1400, delayMillis = 300, easing = EaseOutCubic))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BrutalAccent.copy(alpha = 0.04f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = fadeIn.value
                    translationY = slideUp.value
                }
                .padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.weight(0.25f))

            Text(
                text = "─── ✦ ───",
                fontSize = 20.sp,
                color = BrutalAccent.copy(alpha = 0.7f),
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = chronicle.yearTitle.uppercase(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    lineHeight = 56.sp,
                    fontSize = 44.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = chronicle.mainTheme,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Light,
                    lineHeight = 28.sp
                ),
                color = BrutalAccent.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

            // Stats row - taller numbers
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                MiniStat("${chronicle.totalQuestsCompleted}", "QUESTS")
                MiniStat("${chronicle.totalLoreEntries}", "MEMORIES")
                MiniStat("${chronicle.totalXpEarned}", "XP")
            }

            Spacer(Modifier.weight(0.35f))
        }
    }
}

@Composable
private fun ContentSlide(slide: ChronicleSlide) {
    val fadeIn = remember { Animatable(0f) }
    val textSlide = remember { Animatable(30f) }
    LaunchedEffect(slide) {
        fadeIn.snapTo(0f)
        textSlide.snapTo(30f)
        fadeIn.animateTo(1f, tween(700))
    }
    LaunchedEffect(slide) {
        textSlide.animateTo(0f, tween(900, easing = EaseOutCubic))
    }

    val bgColor = slide.backgroundColor?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { null }
    }

    val hasImage = !slide.imageUri.isNullOrBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        bgColor?.copy(alpha = 0.06f) ?: Color.Transparent,
                        Color.Transparent
                    )
                )
            )
            .graphicsLayer { alpha = fadeIn.value }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .graphicsLayer { translationY = textSlide.value }
        ) {
            Spacer(Modifier.weight(0.15f))

            // Slide type indicator
            Text(
                text = slide.slideType.uppercase().replace("_", " "),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.W300
                ),
                color = BrutalMuted.copy(alpha = 0.4f)
            )

            Spacer(Modifier.height(32.dp))

            // Title - tall and dramatic
            Text(
                text = slide.title.uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    lineHeight = 42.sp,
                    fontSize = 32.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            if (slide.subtitle.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = slide.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Light,
                        lineHeight = 26.sp
                    ),
                    color = BrutalAccent.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Image section
            if (hasImage) {
                Spacer(Modifier.height(28.dp))
                AsyncImage(
                    model = slide.imageUri,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (slide.body.isNotBlank()) {
                Spacer(Modifier.height(if (hasImage) 24.dp else 28.dp))
                Text(
                    text = slide.body,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.W300
                    ),
                    color = BrutalMuted,
                    textAlign = TextAlign.Center
                )
            }

            // Stat display - big and dramatic
            if (slide.statLabel != null && slide.statValue != null) {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = slide.statValue!!,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp
                    ),
                    color = BrutalAccent
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = slide.statLabel!!.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.W300
                    ),
                    color = BrutalMuted.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.weight(0.25f))
        }
    }
}

@Composable
private fun ClosingSlide(chronicle: Chronicle, onDismiss: () -> Unit) {
    val fadeIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeIn.animateTo(1f, tween(1200, delayMillis = 200))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BrutalAccent.copy(alpha = 0.03f),
                        Color.Transparent
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = fadeIn.value }
                .padding(horizontal = 32.dp)
        ) {
            Spacer(Modifier.weight(0.3f))

            Text(
                text = "─── ✦ ───",
                fontSize = 18.sp,
                color = BrutalAccent.copy(alpha = 0.6f),
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "YOUR STORY\nCONTINUES",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    lineHeight = 48.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Best month: ${chronicle.bestMonth}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Light
                ),
                color = BrutalMuted,
                textAlign = TextAlign.Center
            )

            if (chronicle.topAttributes.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = chronicle.topAttributes.joinToString("  ·  "),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.W300
                    ),
                    color = BrutalMuted.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(56.dp))

            Text(
                text = "TAP TO CLOSE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                color = BrutalMuted.copy(alpha = 0.35f)
            )

            Spacer(Modifier.weight(0.35f))
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp,
                fontWeight = FontWeight.W300
            ),
            color = BrutalMuted.copy(alpha = 0.5f)
        )
    }
}
