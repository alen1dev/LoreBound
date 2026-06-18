package com.pauls.lorebound.ui.quest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.theme.XpPurple
import com.pauls.lorebound.ui.components.DifficultyEmberBar
import com.pauls.lorebound.ui.components.TitleUnlockDialog
import com.pauls.lorebound.ui.components.RUNE_DIVIDER_WIDE
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestDetailScreen(
    onBack: () -> Unit,
    viewModel: QuestDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
        }
        return
    }

    val quest = state.quest
    if (quest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("QUEST NOT FOUND", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
        }
        return
    }

    // Title unlock dialog
    state.unlockedTitle?.let { title ->
        TitleUnlockDialog(
            title = title,
            onDismiss = viewModel::dismissTitleDialog
        )
    }

    // Completion dialog
    if (state.showCompletionDialog) {
        QuestCompletionDialog(
            quest = quest,
            state = state,
            onDismiss = viewModel::dismissCompletionDialog,
            onConquer = viewModel::conquerQuest,
            onLoreNotesChange = viewModel::updateLoreNotes,
            onPhotoSelected = viewModel::updatePhotoUri,
            onLinkChange = viewModel::updateLink,
            onLocationCaptured = viewModel::updateLocation,
            onLocationLoading = viewModel::setLocationLoading
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = quest.questType.displayName.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = BrutalMuted
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Success banner
            AnimatedVisibility(
                visible = state.completionSuccess,
                enter = scaleIn() + fadeIn()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CONQUERED",
                        style = MaterialTheme.typography.displayMedium,
                        color = BrutalAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${state.xpAwarded} XP · RANK ${state.rankAfter}",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrutalMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BrutalAccent.copy(alpha = 0.3f), thickness = 1.dp)
                }
            }

            // ── QUEST TITLE — massive ────────────────
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = quest.title.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Meta line with ember bar + info
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyEmberBar(difficulty = quest.difficulty)
                Text(
                    text = difficultyLabel(quest.difficulty),
                    style = MaterialTheme.typography.labelSmall,
                    color = difficultyColor(quest.difficulty)
                )
                Text(
                    text = "~${quest.estimatedMinutes} MIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrutalMuted
                )
            }

            // ── VERIFICATION REQUIREMENT ─────────────
            if (!quest.verificationType.isOptional) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrutalAccent.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "⟐ ${quest.verificationType.requirementLabel.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // ── DESCRIPTION ──────────────────────────
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // ── REWARDS ──────────────────────────
            Text(
                text = "— REWARDS —",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "★ +${quest.xpReward} XP",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                val attributes = listOfNotNull(quest.primaryAttribute, quest.secondaryAttribute)
                attributes.forEach { trait ->
                    Text(
                        text = "${trait.symbol()} +1 ${trait.displayName.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = trait.color()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // ── LORE PROMPT — only shown for TEXT-based verification ──
            if (quest.verificationType.requiresText) {
                Text(
                    text = "— LORE PROMPT —",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "How did you conquer this quest? Tell your story.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrutalMuted,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── CONQUER BUTTON ───────────────────────
            if (!state.isCompleted) {
                Button(
                    onClick = viewModel::showCompletionDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "CONQUER THIS QUEST",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                }
            } else if (!state.completionSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONQUERED",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = BrutalAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


private fun difficultyLabel(level: Int): String = when (level) {
    1 -> "EASY"
    2 -> "MEDIUM"
    3 -> "HARD"
    4 -> "EPIC"
    5 -> "LEGENDARY"
    else -> "UNKNOWN"
}

@Composable
private fun difficultyColor(level: Int): Color = when (level) {
    1 -> QuestGreen
    2 -> BrutalAccent
    3 -> MaterialTheme.colorScheme.error
    4 -> XpPurple
    5 -> Color.White
    else -> BrutalMuted
}
