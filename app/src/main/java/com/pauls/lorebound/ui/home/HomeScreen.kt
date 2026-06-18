package com.pauls.lorebound.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Animatable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.ui.chronicle.ChronicleEntryPointCard
import com.pauls.lorebound.ui.components.DifficultyEmberBar
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalBlack
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.tutorial.QuestTutorialOverlay
import com.pauls.lorebound.ui.tutorial.TutorialPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onQuestClick: (Long) -> Unit,
    onViewChronicle: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshMessage by viewModel.refreshMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showTutorial by remember { mutableStateOf(!TutorialPreferences.hasSeen(context)) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
        }
        return
    }

    val character = state.character ?: return

    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(-20f) }
    LaunchedEffect(Unit) {
        headerAlpha.animateTo(1f, tween(600))
    }
    LaunchedEffect(Unit) {
        headerOffset.animateTo(0f, tween(600))
    }

    // ── Pull-to-refresh ──────────────────
    val pullToRefreshState = rememberPullToRefreshState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize(),
            indicator = {
                // Custom indicator instead of default spinner
                val progress = pullToRefreshState.distanceFraction
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when {
                                refreshMessage != null -> refreshMessage!!
                                isRefreshing -> "◈  REFRESHING  ◈"
                                progress > 0.8f -> "RELEASE"
                                progress > 0f -> "↓  PULL  ↓"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                refreshMessage == "NEW QUESTS FOUND" -> QuestGreen
                                refreshMessage != null -> BrutalAccent
                                isRefreshing -> BrutalAccent
                                progress > 0.8f -> Color.White
                                else -> BrutalMuted.copy(alpha = progress)
                            },
                            textAlign = TextAlign.Center
                        )
                        if (isRefreshing) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ◆ —",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrutalSubtle
                            )
                        }
                    }
                }
            }
        ) {
            // Content moves down slightly with pull
            val contentOffset = pullToRefreshState.distanceFraction * 60f

            Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        translationY = contentOffset
                    }
            ) {
            item { Spacer(modifier = Modifier.height(48.dp)) }

            // ── CHARACTER IDENTITY ─────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = headerAlpha.value
                            translationY = headerOffset.value
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.name.uppercase(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                shadow = Shadow(
                                    color = BrutalAccent.copy(alpha = 0.6f),
                                    blurRadius = 12f
                                )
                            ),
                            color = Color.White
                        )
                        character.currentTitle?.let { title ->
                            Text(
                                text = title.uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                color = BrutalAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RANK ${state.rank} · ${character.totalXp} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrutalMuted
                        )
                    }

                    // Chronicle pill button (right side)
                    ChronicleEntryPointCard(
                        state = state.chronicleState,
                        onViewChronicle = onViewChronicle
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(36.dp)) }

            // ── TODAY'S QUEST — hero placement ─────────
            item {
                HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "TODAY'S QUEST",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.dailyQuests.isEmpty()) {
                item {
                    Text(
                        text = "No quest awaits today.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrutalMuted,
                        fontStyle = FontStyle.Italic
                    )
                }
            } else {
                items(
                    items = state.dailyQuests,
                    key = { it.activeQuest.id }
                ) { item ->
                    QuestCard(
                        quest = item.quest,
                        isCompleted = item.activeQuest.isCompleted,
                        isHero = true,
                        onClick = { onQuestClick(item.activeQuest.id) }
                    )
                }
            }

            // ── SIDE QUESTS ────────────────────────────
            if (state.sideQuests.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                    text = "SIDE QUEST",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(
                    items = state.sideQuests,
                    key = { it.activeQuest.id }
                ) { item ->
                    QuestCard(
                        quest = item.quest,
                        isCompleted = item.activeQuest.isCompleted,
                        onClick = { onQuestClick(item.activeQuest.id) }
                    )
                }
            }

            // ── ADVENTURE ──────────────────────────────
            if (state.adventures.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                    text = "ADVENTURE",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(
                    items = state.adventures,
                    key = { it.activeQuest.id }
                ) { item ->
                    QuestCard(
                        quest = item.quest,
                        isCompleted = item.activeQuest.isCompleted,
                        onClick = { onQuestClick(item.activeQuest.id) }
                    )
                }
            }

            // ── EPIC ───────────────────────────────────
            if (state.epics.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                    text = "EPIC",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(
                    items = state.epics,
                    key = { it.activeQuest.id }
                ) { item ->
                    QuestCard(
                        quest = item.quest,
                        isCompleted = item.activeQuest.isCompleted,
                        onClick = { onQuestClick(item.activeQuest.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

            // Top fade overlay
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrutalBlack,
                                Color.Transparent
                            )
                        )
                    )
            )
            } // inner Box
        } // PullToRefreshBox
    } // Surface

    // Tutorial overlay for first-time users (Dialog-based, renders above everything)
    if (showTutorial) {
        QuestTutorialOverlay(
            onDismiss = { showTutorial = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestCard(
    quest: com.pauls.lorebound.domain.model.Quest,
    isCompleted: Boolean,
    isHero: Boolean = false,
    onClick: () -> Unit
) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(30f) }
    LaunchedEffect(Unit) {
        animatedAlpha.animateTo(1f, tween(400))
    }
    LaunchedEffect(Unit) {
        animatedOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) BrutalAccent.copy(alpha = 0.04f) else BrutalSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Quest title — large for hero, medium otherwise
            Text(
                text = quest.title.uppercase(),
                style = if (isHero) {
                    MaterialTheme.typography.headlineLarge
                } else {
                    MaterialTheme.typography.titleLarge
                },
                color = if (isCompleted) BrutalMuted else Color.White,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Reward + difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // XP reward
                Text(
                    text = "+${quest.xpReward} XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isCompleted) BrutalMuted else BrutalAccent,
                    fontWeight = FontWeight.Bold
                )

                if (isCompleted) {
                    Text(
                        text = "CONQUERED",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalAccent
                    )
                } else {
                    DifficultyEmberBar(
                        difficulty = quest.difficulty,
                        barWidth = 3,
                        barHeight = 10
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trait tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quest.let { q -> listOfNotNull(q.primaryAttribute, q.secondaryAttribute) }.forEach { trait ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BrutalSubtle.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = trait.symbol(),
                                style = MaterialTheme.typography.labelSmall,
                                color = trait.color()
                            )
                            Text(
                                text = trait.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = BrutalMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
