package com.pauls.lorebound.ui.lore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.ui.components.RUNE_DIVIDER_WIDE
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.DangerRed
import com.pauls.lorebound.ui.theme.QuestGreen
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoreEntryDetailScreen(
    onBack: () -> Unit,
    viewModel: LoreEntryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
        }
        return
    }

    val entry = state.entry
    if (entry == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("LORE ENTRY NOT FOUND", color = DangerRed, style = MaterialTheme.typography.labelMedium)
        }
        return
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "ERASE THIS MEMORY",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Text(
                    "This memory will be lost to the void, never to return. Are you certain?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrutalMuted,
                    fontStyle = FontStyle.Italic
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteEntry(onBack) }
                ) {
                    Text("ERASE", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("KEEP", color = BrutalMuted)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MEMORY",
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
                actions = {
                    // Favorite toggle
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (entry?.isFavorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (entry?.isFavorite == true) BrutalAccent else BrutalMuted
                        )
                    }
                    if (!state.isEditing) {
                        IconButton(onClick = viewModel::startEditing) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = DangerRed
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Decorative rune line
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date
            Text(
                text = formatDetailDate(entry.date).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = BrutalAccent,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quest title
            Text(
                text = entry.questTitle.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Photo
            if (!entry.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = entry.photoUri,
                    contentDescription = "Memory photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrutalSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // XP and Rank row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "XP",
                        tint = BrutalAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "+${entry.xpEarned} XP",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Text(
                        text = "EXPERIENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.MilitaryTech,
                        contentDescription = "Rank",
                        tint = BrutalAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "${entry.rankAtCompletion}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Text(
                        text = "RANK",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Traits improved
            if (entry.traitsImproved.isNotEmpty()) {
                Text(
                    text = "— GROWTH —",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.traitsImproved.forEach { trait ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = trait.color().copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${trait.symbol()} +1 ${trait.displayName.uppercase()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = trait.color(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lore notes — the memory itself
            Text(
                text = "— THE MEMORY —",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isEditing) {
                OutlinedTextField(
                    value = state.editedNotes,
                    onValueChange = viewModel::updateNotes,
                    placeholder = { Text("What do you remember about this adventure?", color = BrutalMuted) },
                    minLines = 4,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = viewModel::cancelEditing) {
                        Text("CANCEL", color = BrutalMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = viewModel::saveNotes,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("SAVE", fontWeight = FontWeight.Black)
                    }
                }
            } else {
                val notes = entry.userNotes
                if (notes.isNullOrBlank()) {
                    Text(
                        text = "This adventure passed without a written memory.\nTap the edit icon to record what you remember.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrutalMuted,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 22.sp
                    )
                } else {
                    Text(
                        text = "\u201C$notes\u201D",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        lineHeight = 26.sp
                    )
                }
            }

            // Location
            if (entry.latitude != null && entry.longitude != null) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = BrutalAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "%.4f, %.4f".format(entry.latitude, entry.longitude),
                        style = MaterialTheme.typography.labelMedium,
                        color = BrutalMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = RUNE_DIVIDER_WIDE,
                style = MaterialTheme.typography.labelSmall,
                color = BrutalSubtle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun formatDetailDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        val date = parser.parse(isoDate)
        if (date != null) formatter.format(date) else isoDate
    } catch (_: Exception) {
        isoDate
    }
}

