package com.pauls.lorebound.ui.showcase

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pauls.lorebound.AppConfig
import com.pauls.lorebound.notifications.QuestReminderScheduler
import com.pauls.lorebound.domain.ai.AiKeyStore
import com.pauls.lorebound.domain.ai.AiProviderType
import com.pauls.lorebound.domain.ai.GeminiProvider
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.DangerRed
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.tutorial.TutorialPreferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// ── ViewModel for Settings page API setup ─────────────────

data class SettingsState(
    val selectedProvider: AiProviderType = AiProviderType.DISABLED,
    val apiKey: String = "",
    val isKeyVisible: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val aiKeyStore: AiKeyStore,
    private val geminiProvider: GeminiProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                selectedProvider = aiKeyStore.getSelectedProvider(),
                apiKey = aiKeyStore.getGeminiApiKey() ?: ""
            )
        }
    }

    fun setProvider(type: AiProviderType) {
        aiKeyStore.setSelectedProvider(type)
        _state.update { it.copy(selectedProvider = type) }
    }

    fun setApiKey(key: String) {
        _state.update { it.copy(apiKey = key) }
        aiKeyStore.setGeminiApiKey(key)
    }

    fun toggleKeyVisibility() {
        _state.update { it.copy(isKeyVisible = !it.isKeyVisible) }
    }

    fun testConnection() {
        viewModelScope.launch {
            _state.update { it.copy(isTesting = true, testResult = null) }
            val result = withContext(Dispatchers.IO) { geminiProvider.testConnection() }
            _state.update {
                it.copy(
                    isTesting = false,
                    testResult = if (result.success) "✓ Connected (${result.latencyMs}ms)" else "✗ ${result.message}"
                )
            }
        }
    }
}

// ── Settings Screen ───────────────────────────────────────

@Composable
fun StyleShowcaseScreen(
    onNavigateToDeveloperMenu: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val contentAlpha = remember { Animatable(0f) }
    val contentOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(600)) }
    LaunchedEffect(Unit) { contentOffset.animateTo(0f, tween(500)) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
            Spacer(Modifier.height(48.dp))

            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            Spacer(Modifier.height(32.dp))

            // ── AI PROVIDER SETUP (available to everyone) ──
            SectionHeader("AI PROVIDER")

            Text(
                text = "Set up your Gemini API key to enable Chronicle generation at year end.",
                style = MaterialTheme.typography.bodySmall,
                color = BrutalMuted,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            // How to get a key
            Text(
                text = "HOW TO SET UP",
                style = MaterialTheme.typography.labelSmall,
                color = BrutalAccent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "1. Go to aistudio.google.com\n2. Sign in with your Google account\n3. Click 'Get API Key'\n4. Create a new key and copy it\n5. Paste it below and tap Test Connection",
                style = MaterialTheme.typography.bodySmall,
                color = BrutalMuted,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(16.dp))

            // Provider selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AiProviderType.entries.forEach { provider ->
                    FilterChip(
                        selected = state.selectedProvider == provider,
                        onClick = { viewModel.setProvider(provider) },
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

            // API Key input (when Gemini selected)
            if (state.selectedProvider == AiProviderType.GEMINI) {
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.apiKey,
                    onValueChange = viewModel::setApiKey,
                    label = { Text("Gemini API Key", color = BrutalMuted) },
                    singleLine = true,
                    visualTransformation = if (state.isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleKeyVisibility) {
                            Icon(
                                if (state.isKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
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

                Button(
                    onClick = viewModel::testConnection,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrutalSurfaceVariant,
                        contentColor = Color.White
                    )
                ) {
                    Text("TEST CONNECTION", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }

                if (state.isTesting) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        CircularProgressIndicator(color = BrutalAccent, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Testing...", style = MaterialTheme.typography.labelSmall, color = BrutalMuted)
                    }
                }

                state.testResult?.let { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (result.startsWith("✓")) QuestGreen else DangerRed,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── NOTIFICATIONS ─────────────────────────
            SectionHeader("NOTIFICATIONS")

            NotificationToggleRow()

            Spacer(Modifier.height(32.dp))

            // ── TUTORIAL ─────────────────────────────
            SectionHeader("TUTORIAL")

            Text(
                text = "Replay the quest board walkthrough to learn about daily, weekly, and monthly quests.",
                style = MaterialTheme.typography.bodySmall,
                color = BrutalMuted,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    TutorialPreferences.reset(context)
                    onNavigateToHome()
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrutalSurfaceVariant,
                    contentColor = Color.White
                )
            ) {
                Text(
                text = "REPLAY QUEST TUTORIAL", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(Modifier.height(32.dp))

            // ── ABOUT ─────────────────────────────────
            SectionHeader("ABOUT")

            Text(
                text = "LoreBound transforms your daily life into an RPG adventure. Complete quests, grow your character, and relive your year through the Chronicle.",
                style = MaterialTheme.typography.bodySmall,
                color = BrutalMuted,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            // ── DEVELOPER TOOLS (only if unlocked) ────
            if (AppConfig.isDevModeUnlocked) {
                SectionHeader("DEVELOPER TOOLS")

                Text(
                    text = "Developer mode is active.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrutalAccent
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToDeveloperMenu,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrutalSurfaceVariant,
                        contentColor = Color.White
                    )
                ) {
                    Text("OPEN DEVELOPER MENU", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
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
private fun NotificationToggleRow() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("lorebound_settings", android.content.Context.MODE_PRIVATE)
    }
    var enabled by remember { mutableStateOf(prefs.getBoolean("notifications_enabled", true)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Weekly Quest Reminder",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = "Friday 6 PM — check new quests & lore paths",
                style = MaterialTheme.typography.labelSmall,
                color = BrutalMuted
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = { checked ->
                enabled = checked
                prefs.edit().putBoolean("notifications_enabled", checked).apply()
                if (checked) {
                    QuestReminderScheduler.schedule(context)
                } else {
                    QuestReminderScheduler.cancel(context)
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = BrutalAccent,
                checkedTrackColor = BrutalAccent.copy(alpha = 0.3f),
                uncheckedThumbColor = BrutalMuted,
                uncheckedTrackColor = BrutalSubtle
            )
        )
    }
}

