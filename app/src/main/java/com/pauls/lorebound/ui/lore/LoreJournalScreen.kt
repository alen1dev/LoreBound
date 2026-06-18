package com.pauls.lorebound.ui.lore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalBlack
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.theme.XpPurple
import com.pauls.lorebound.ui.tutorial.TutorialPreferences
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LoreJournalScreen(
    onEntryClick: (Long) -> Unit,
    onCreatePersonalLore: () -> Unit,
    viewModel: LoreJournalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showLoreTutorial by remember { mutableStateOf(!TutorialPreferences.hasSeenLore(context)) }

    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(-20f) }
    LaunchedEffect(Unit) {
        headerAlpha.animateTo(1f, tween(600))
    }
    LaunchedEffect(Unit) {
        headerOffset.animateTo(0f, tween(600))
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrutalAccent, strokeWidth = 2.dp)
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePersonalLore,
                modifier = Modifier.padding(bottom = 80.dp),
                containerColor = BrutalAccent,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Record Lore")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val overscrollEffect = rememberOverscrollEffect()
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .overscroll(overscrollEffect),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                overscrollEffect = overscrollEffect
            ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Header
            item {
                Column(
                    modifier = Modifier.graphicsLayer {
                        alpha = headerAlpha.value
                        translationY = headerOffset.value
                    }
                ) {
                    Text(
                        text = "LORE\nJOURNAL",
                        style = MaterialTheme.typography.displayLarge.copy(
                            shadow = Shadow(
                                color = BrutalAccent.copy(alpha = 0.6f),
                                blurRadius = 12f
                            )
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "${state.totalEntries} MEMORIES",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrutalMuted
                        )
                        if (state.totalXp > 0) {
                            Text(
                                text = "${state.totalXp} XP EARNED",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrutalAccent
                            )
                        }
                    }
                }
            }

            // Search bar
            item {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = {
                        Text("Search your memories...", color = BrutalMuted)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = BrutalMuted
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = BrutalSurfaceVariant,
                        focusedContainerColor = BrutalSurfaceVariant,
                        unfocusedBorderColor = BrutalSubtle,
                        focusedBorderColor = BrutalAccent,
                        cursorColor = BrutalAccent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Filter chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LoreFilter.entries.forEach { filter ->
                        val selected = state.filter == filter
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setFilter(filter) },
                            label = {
                                Text(
                                    text = when (filter) {
                                        LoreFilter.ALL -> "ALL"
                                        LoreFilter.QUEST -> "QUESTS"
                                        LoreFilter.PERSONAL -> "PERSONAL"
                                        LoreFilter.FAVORITES -> "♥"
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = BrutalSurfaceVariant,
                                selectedContainerColor = BrutalAccent.copy(alpha = 0.15f),
                                labelColor = BrutalMuted,
                                selectedLabelColor = BrutalAccent
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = BrutalSubtle,
                                selectedBorderColor = BrutalAccent,
                                enabled = true,
                                selected = selected
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.entries.isEmpty()) {
                item { EmptyJournal() }
            } else {
                state.groupedEntries.forEach { (date, entries) ->
                    item { DateChapter(date) }
                    items(
                        items = entries,
                        key = { it.id }
                    ) { entry ->
                        MemoryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry.id) }
                        )
                    }
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
                            colors = listOf(BrutalBlack, Color.Transparent)
                        )
                    )
            )
        }
    }

    // Lore Journal tutorial for first-time visitors
    if (showLoreTutorial) {
        LoreJournalTutorialDialog(
            onDismiss = {
                TutorialPreferences.markSeenLore(context)
                showLoreTutorial = false
            }
        )
    }
}

@Composable
private fun DateChapter(isoDate: String) {
    Column(modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)) {
        HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = formatChapterDate(isoDate),
            style = MaterialTheme.typography.labelMedium,
            color = BrutalAccent,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemoryCard(
    entry: LoreEntry,
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
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            },
        colors = CardDefaults.cardColors(containerColor = BrutalSurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            // Photo as darkened card background
            if (!entry.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = entry.photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                )
                // Dark scrim overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.72f))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
            // Badge + Title row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Entry type badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (entry.isPersonal) BrutalAccent.copy(alpha = 0.12f)
                    else XpPurple.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (entry.isPersonal) "PERSONAL" else "QUEST",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (entry.isPersonal) BrutalAccent else XpPurple,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (entry.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        tint = BrutalAccent,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = entry.displayTitle.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // User notes / story
            entry.userNotes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\u201C$notes\u201D",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrutalMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row: tags/traits + indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tags for personal, traits for quest
                if (entry.isPersonal && entry.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        entry.tags.take(3).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BrutalSubtle.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = tag.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = BrutalMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                } else if (entry.traitsImproved.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        entry.traitsImproved.forEach { trait ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BrutalSubtle.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
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
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Right side indicators
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!entry.photoUri.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Has photo",
                            tint = BrutalMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .alpha(0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (entry.latitude != null) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Has location",
                            tint = BrutalMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .alpha(0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (entry.xpEarned > 0) {
                        Text(
                            text = "+${entry.xpEarned} XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrutalAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            } // Column
        } // Box
    } // Card
}

@Composable
private fun EmptyJournal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BrutalSubtle, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoStories,
                contentDescription = null,
                tint = BrutalAccent,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "NO LORE YET",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Conquer quests or record personal memories\nto fill these pages.",
            style = MaterialTheme.typography.bodyMedium,
            color = BrutalMuted,
            lineHeight = 22.sp
        )
    }
}

private fun formatChapterDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val date = parser.parse(isoDate)
        if (date != null) formatter.format(date).uppercase(Locale.US) else isoDate
    } catch (_: Exception) {
        isoDate
    }
}
