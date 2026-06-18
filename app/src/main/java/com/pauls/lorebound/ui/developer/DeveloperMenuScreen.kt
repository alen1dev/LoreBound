package com.pauls.lorebound.ui.developer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.AppConfig
import com.pauls.lorebound.domain.ai.AiProviderType
import com.pauls.lorebound.domain.model.QuestCategory
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.ui.components.TitleUnlockDialog
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.DangerRed
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.notifications.WeeklyQuestReminderWorker
import com.pauls.lorebound.notifications.ChronicleReadyWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperMenuScreen(
    onBack: () -> Unit,
    onFreshStart: () -> Unit,
    onRestartHome: () -> Unit,
    viewModel: DeveloperMenuViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showApiKeyHelp by remember { mutableStateOf(false) }
    var showChronicleJson by remember { mutableStateOf(false) }
    var showTitleUnlockTest by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    val contentAlpha = remember { Animatable(0f) }
    val contentOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(600)) }
    LaunchedEffect(Unit) { contentOffset.animateTo(0f, tween(500)) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DevMenuEvent.NavigateToCharacterCreation -> onFreshStart()
                DevMenuEvent.RestartHome -> onRestartHome()
                is DevMenuEvent.ShowMessage -> { /* Could show snackbar */ }
            }
        }
    }

    if (showApiKeyHelp) {
        ApiKeyHelpDialog(onDismiss = { showApiKeyHelp = false })
    }

    if (showChronicleJson && state.chronicleJson != null) {
        ChronicleJsonDialog(json = state.chronicleJson!!, onDismiss = { showChronicleJson = false })
    }

    if (showTitleUnlockTest) {
        TitleUnlockDialog(
            title = Title(
                id = "test",
                name = "Wanderer",
                description = "Awarded to those who take the first steps into the unknown.",
                category = QuestCategory.EXPLORATION,
                requiredQuestCount = 1,
                isUnlocked = true
            ),
            onDismiss = { showTitleUnlockTest = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("DEVELOPER", style = MaterialTheme.typography.labelMedium, color = BrutalMuted)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                .graphicsLayer {
                    alpha = contentAlpha.value
                    translationY = contentOffset.value
                }
        ) {
            // Mode indicator
            Text(
                text = "MODE: DEVELOPER",
                style = MaterialTheme.typography.labelSmall,
                color = BrutalAccent
            )
            Text(
                text = "SIMULATED DATE: ${state.currentSimulatedDate}",
                style = MaterialTheme.typography.labelSmall,
                color = BrutalMuted
            )
            if (state.timeOffsetDays != 0L) {
                Text(
                    text = "TIME OFFSET: +${state.timeOffsetDays} DAYS",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrutalAccent
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── AI TOOLS ──────────────────────────────
            DevSection("AI TOOLS")

            // Provider selector
            Text("Provider", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AiProviderType.entries.forEach { provider ->
                    FilterChip(
                        selected = state.selectedProvider == provider,
                        onClick = { viewModel.setAiProvider(provider) },
                        label = { Text(provider.name, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrutalAccent.copy(alpha = 0.15f),
                            selectedLabelColor = BrutalAccent,
                            containerColor = BrutalSurfaceVariant,
                            labelColor = BrutalMuted
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // API Key input
            if (state.selectedProvider == AiProviderType.GEMINI) {
                OutlinedTextField(
                    value = state.geminiApiKey,
                    onValueChange = viewModel::setGeminiApiKey,
                    label = { Text("Gemini API Key", color = BrutalMuted) },
                    singleLine = true,
                    visualTransformation = if (state.isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleApiKeyVisibility) {
                            Icon(
                                if (state.isApiKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = BrutalMuted
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
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

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DevButton("TEST CONNECTION", onClick = viewModel::testConnection, modifier = Modifier.weight(1f))
                    DevButton("HOW TO GET KEY", onClick = { showApiKeyHelp = true }, modifier = Modifier.weight(1f))
                }

                // Connection test result
                if (state.isTestingConnection) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        CircularProgressIndicator(color = BrutalAccent, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Testing...", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                    }
                }
                state.connectionTestResult?.let { result ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        SelectionContainer(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (result.startsWith("✓")) QuestGreen else DangerRed
                            )
                        }
                        if (!result.startsWith("✓")) {
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(result)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = "Copy error",
                                    tint = BrutalMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── CHRONICLE TESTING ─────────────────────
            DevSection("CHRONICLE TESTING")

            if (state.selectedProvider == AiProviderType.DISABLED) {
                Text(
                    text = "Enable an AI provider above to test Chronicles.",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrutalMuted
                )
            } else {
                DevButton("GENERATE CHRONICLE JSON", onClick = viewModel::generateChronicleJson)

                if (state.isGeneratingChronicle) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        CircularProgressIndicator(color = BrutalAccent, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Generating Chronicle...", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                    }
                }

                state.chronicleError?.let { error ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        SelectionContainer(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Error: $error",
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed
                            )
                        }
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(error)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = "Copy error",
                                tint = BrutalMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                state.chroniclePreview?.let { chronicle ->
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = BrutalSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(chronicle.yearTitle, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text("Theme: ${chronicle.mainTheme}", style = MaterialTheme.typography.labelSmall, color = BrutalAccent)
                            Text("Best Month: ${chronicle.bestMonth}", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                            Text("Slides: ${chronicle.slides.size}", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                        }
                    }
                }

                if (state.chronicleJson != null) {
                    Spacer(Modifier.height(4.dp))
                    DevButton("INSPECT JSON", onClick = { showChronicleJson = true })
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── YEAR-END SIMULATION ───────────────────
            DevSection("YEAR-END SIMULATION")
            DevButton("SIMULATE DECEMBER 1", onClick = viewModel::simulateDecember1)
            DevButton("SIMULATE DECEMBER 15", onClick = viewModel::simulateDecember24)
            DevButton("SIMULATE JANUARY 16", onClick = viewModel::simulateJanuary1)
            DevButton("SIMULATE END OF YEAR", onClick = viewModel::simulateEndOfYear)

            Spacer(Modifier.height(24.dp))

            // ── DEV UTILITIES ─────────────────────────
            DevSection("DEV UTILITIES")
            DevButton("GENERATE FAKE YEAR", onClick = viewModel::generateFakeYear)

            if (state.isFakeYearGenerating) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    CircularProgressIndicator(color = BrutalAccent, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generating...", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                }
            }
            state.fakeYearMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = QuestGreen,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            DevButton("RESET CHRONICLE TEST DATA", onClick = viewModel::resetChronicleData, isDanger = true)

            Spacer(Modifier.height(24.dp))

            // ── TIME CONTROLS ───────────────────────
            DevSection("TIME CONTROLS")
            DevButton("ADVANCE 1 DAY", viewModel::advanceOneDay)
            DevButton("ADVANCE 1 WEEK", viewModel::advanceOneWeek)
            DevButton("ADVANCE 1 MONTH", viewModel::advanceOneMonth)
            DevButton("ADVANCE 3 MONTHS", viewModel::advanceThreeMonths)
            DevButton("ADVANCE 1 YEAR", viewModel::advanceOneYear)
            DevButton("RESET TIME", viewModel::resetTime)

            Spacer(Modifier.height(24.dp))

            // ── CHARACTER CONTROLS ──────────────────
            DevSection("CHARACTER CONTROLS")
            DevButton("GRANT 100 XP", onClick = { viewModel.grantXp(100) })
            DevButton("GRANT 500 XP", onClick = { viewModel.grantXp(500) })
            DevButton("GRANT 1000 XP", onClick = { viewModel.grantXp(1000) })
            DevButton("SET RANK 5", onClick = { viewModel.setRank(5) })
            DevButton("SET RANK 10", onClick = { viewModel.setRank(10) })
            DevButton("SET RANK 25", onClick = { viewModel.setRank(25) })
            DevButton("UNLOCK ALL TITLES", viewModel::unlockAllTitles)
            DevButton("TEST TITLE UNLOCK REVEAL", onClick = { showTitleUnlockTest = true })
            DevButton("UNLOCK ALL ACHIEVEMENTS", viewModel::unlockAllFeats)

            Spacer(Modifier.height(24.dp))

            // ── QUEST CONTROLS ──────────────────────
            DevSection("QUEST CONTROLS")
            DevButton("GENERATE NEW DAILY", viewModel::generateNewDaily)
            DevButton("GENERATE NEW SIDE QUEST", viewModel::generateNewSideQuest)
            DevButton("GENERATE NEW ADVENTURE", viewModel::generateNewAdventure)
            DevButton("GENERATE NEW EPIC", viewModel::generateNewEpic)

            Spacer(Modifier.height(24.dp))

            // ── DATABASE CONTROLS ───────────────────
            DevSection("DATABASE CONTROLS")
            DevButton("RESET DATABASE", viewModel::resetDatabase, isDanger = true)

            Spacer(Modifier.height(24.dp))

            // ── NOTIFICATION TESTS ──────────────────
            DevSection("NOTIFICATION TESTS")
            DevButton("TEST WEEKLY QUEST NOTIFICATION", onClick = {
                val workRequest = OneTimeWorkRequestBuilder<WeeklyQuestReminderWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            })
            DevButton("TEST CHRONICLE NOTIFICATION", onClick = {
                val workRequest = OneTimeWorkRequestBuilder<ChronicleReadyWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            })

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ApiKeyHelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BrutalSurfaceVariant,
        title = { Text("How to Get a Gemini API Key", color = Color.White) },
        text = {
            Column {
                val steps = listOf(
                    "1. Open Google AI Studio\n   (aistudio.google.com)",
                    "2. Sign in with your Google account",
                    "3. Click 'Get API Key' in the sidebar",
                    "4. Create a new API key",
                    "5. Copy the key",
                    "6. Paste it into Lorebound"
                )
                steps.forEach { step ->
                    Text(step, style = MaterialTheme.typography.bodySmall, color = BrutalMuted)
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Note: Free tier allows 15 RPM / 1M TPM", style = MaterialTheme.typography.labelSmall, color = BrutalAccent)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("GOT IT", color = BrutalAccent)
            }
        }
    )
}

@Composable
private fun ChronicleJsonDialog(json: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BrutalSurfaceVariant,
        title = { Text("Chronicle JSON", color = Color.White) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = json,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = BrutalMuted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = BrutalAccent)
            }
        }
    )
}

@Composable
private fun DevSection(title: String) {
    HorizontalDivider(color = BrutalSubtle, thickness = 0.5.dp)
    Spacer(Modifier.height(12.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = BrutalMuted
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun DevButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .height(44.dp),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDanger) DangerRed.copy(alpha = 0.12f) else BrutalSurfaceVariant,
            contentColor = if (isDanger) DangerRed else Color.White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
