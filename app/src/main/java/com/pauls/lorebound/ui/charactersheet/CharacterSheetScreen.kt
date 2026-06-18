package com.pauls.lorebound.ui.charactersheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.AppConfig
import com.pauls.lorebound.ui.components.RUNE_DIVIDER_WIDE
import com.pauls.lorebound.ui.components.RadarChart
import com.pauls.lorebound.ui.components.TraitData
import com.pauls.lorebound.ui.components.code
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalBlack
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterSheetScreen(
    viewModel: CharacterSheetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
        }
        return
    }

    val character = state.character ?: return

    var devTapCount by remember { mutableIntStateOf(0) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false; passwordInput = ""; passwordError = false },
            title = { Text("DEVELOPER ACCESS", color = Color.White) },
            text = {
                Column {
                    Text("Enter developer password:", color = BrutalMuted, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; passwordError = false },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrutalAccent,
                            unfocusedBorderColor = BrutalSubtle,
                            cursorColor = BrutalAccent
                        )
                    )
                    if (passwordError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Incorrect password", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (passwordInput == AppConfig.DEV_PASSWORD) {
                        AppConfig.unlockDevMode()
                        showPasswordDialog = false
                        passwordInput = ""
                    } else {
                        passwordError = true
                    }
                }) { Text("UNLOCK", color = BrutalAccent) }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false; passwordInput = "" }) {
                    Text("CANCEL", color = BrutalMuted)
                }
            },
            containerColor = BrutalSurfaceVariant
        )
    }

    val contentAlpha = remember { Animatable(0f) }
    val contentOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(600))
    }
    LaunchedEffect(Unit) {
        contentOffset.animateTo(0f, tween(500))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentOffset.value
                    }
            ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── DECORATIVE HEADER ────────────────────
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ── NAME ─────────────────────────────────
            Text(
                text = character.name.uppercase(),
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = Shadow(
                        color = BrutalAccent.copy(alpha = 0.6f),
                        blurRadius = 12f
                    )
                ),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        devTapCount++
                        if (devTapCount >= 7 && !AppConfig.isDevModeUnlocked) {
                            devTapCount = 0
                            showPasswordDialog = true
                        }
                    },
                textAlign = TextAlign.Center
            )
            character.currentTitle?.let { title ->
                Text(
                    text = "« ${title.uppercase()} »",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalAccent,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── STATS ROW — with Material icons ─────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SymbolStat(icon = Icons.Rounded.MilitaryTech, value = "${state.rank}", label = "RANK")
                SymbolStat(icon = Icons.Rounded.AutoAwesome, value = "${character.totalXp}", label = "XP")
                SymbolStat(icon = Icons.Rounded.EmojiEvents, value = "${state.totalQuestsCompleted}", label = "QUESTS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── XP PROGRESS ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(BrutalSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.rankProgress)
                        .height(2.dp)
                        .background(Color.White)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RANK ${state.rank + 1}", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                Text("${state.xpToNextRank} XP TO GO", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── STREAKS ──────────────────────────────
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SymbolStat(icon = Icons.Rounded.LocalFireDepartment, value = "${character.currentStreak}", label = "CURRENT STREAK")
                SymbolStat(icon = Icons.Rounded.FlashOn, value = "${character.longestStreak}", label = "LONGEST STREAK")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── TRAITS — Warhorn style grid ──────────
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "— TRAITS —",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrutalSurfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top row: STR, INT, CHA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Trait.entries.take(3).forEach { trait ->
                            TraitStatCell(trait = trait, value = character.traitValue(trait))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Bottom row: CRE, EXP, COU
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Trait.entries.drop(3).forEach { trait ->
                            TraitStatCell(trait = trait, value = character.traitValue(trait))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Radar chart
            val traitData = Trait.entries.map { trait ->
                TraitData(trait = trait, value = character.traitValue(trait))
            }
            RadarChart(
                traits = traitData,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Trait bars — thin brutal lines with symbols
            Trait.entries.forEach { trait ->
                BrutalTraitBar(
                    symbol = trait.symbol(),
                    label = trait.displayName.uppercase(),
                    value = character.traitValue(trait),
                    maxValue = 30,
                    accentColor = trait.color()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── TITLES ───────────────────────────────
            if (state.unlockedTitles.isNotEmpty()) {
                HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "— EARNED TITLES —",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.unlockedTitles.forEach { title ->
                        Text(
                            text = "« ${title.name.uppercase()} »",
                            style = MaterialTheme.typography.labelLarge,
                            color = BrutalAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(80.dp))
        }

            // Top fade overlay
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrutalBlack, Color.Transparent)
                        )
                    )
            )
        }
    }
}

@Composable
private fun SymbolStat(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = BrutalAccent,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            ),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BrutalMuted
        )
    }
}

@Composable
private fun TraitStatCell(
    trait: Trait,
    value: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = trait.symbol(),
            fontSize = 16.sp,
            color = trait.color()
        )
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = Color.White
        )
        Text(
            text = trait.code(),
            style = MaterialTheme.typography.labelSmall,
            color = BrutalMuted
        )
    }
}

@Composable
private fun BrutalTraitBar(
    symbol: String,
    label: String,
    value: Int,
    maxValue: Int,
    accentColor: Color = BrutalAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = symbol,
            fontSize = 12.sp,
            color = accentColor,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BrutalMuted,
            modifier = Modifier.width(90.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(BrutalSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value.toFloat() / maxValue).coerceIn(0f, 1f))
                    .height(2.dp)
                    .background(accentColor)
            )
        }
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = Color.White,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )
    }
}
