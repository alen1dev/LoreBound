package com.pauls.lorebound.ui.character

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.DangerRed
import com.pauls.lorebound.ui.theme.QuestGreen
import com.pauls.lorebound.ui.components.RUNE_DIVIDER_WIDE
import com.pauls.lorebound.ui.components.code
import com.pauls.lorebound.ui.components.color
import com.pauls.lorebound.ui.components.symbol

@Composable
fun CharacterCreationScreen(
    onCharacterCreated: () -> Unit,
    viewModel: CharacterCreationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) onCharacterCreated()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "step_transition"
        ) { step ->
            when (step) {
                1 -> NameStep(
                    name = state.name,
                    nameError = state.nameError,
                    onNameChange = viewModel::updateName,
                    onContinue = viewModel::proceedToStep2
                )
                2 -> StrengthStep(
                    state = state,
                    onToggleStrength = viewModel::toggleStrength,
                    onBack = viewModel::goBackToStep1,
                    onContinue = viewModel::proceedToStep3
                )
                3 -> WeaknessStep(
                    state = state,
                    onToggleWeakness = viewModel::toggleWeakness,
                    onBack = viewModel::goBackToStep2,
                    onCreate = viewModel::createCharacter
                )
            }
        }
    }
}

@Composable
private fun NameStep(
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = RUNE_DIVIDER_WIDE,
            style = MaterialTheme.typography.labelSmall,
            color = BrutalSubtle
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LOREBOUND",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "EVERY LEGEND BEGINS WITH A NAME",
            style = MaterialTheme.typography.labelMedium,
            color = BrutalMuted
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "WHAT SHALL THE\nWORLD CALL YOU?",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("CHARACTER NAME") },
            singleLine = true,
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onContinue() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = BrutalSubtle,
                cursorColor = Color.White,
                focusedLabelColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinue,
            enabled = name.isNotBlank() && name.length >= 2,
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
                text = "CONTINUE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StrengthStep(
    state: CharacterCreationState,
    onToggleStrength: (Trait) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "YOUR GREATEST\nSTRENGTHS",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "PICK 2 TRAITS YOU EXCEL AT",
            style = MaterialTheme.typography.labelSmall,
            color = BrutalMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "THESE START AT 8",
            style = MaterialTheme.typography.labelSmall,
            color = QuestGreen
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Trait.entries.forEach { trait ->
                val isSelected = trait in state.strongTraits
                BrutalTraitCard(
                    trait = trait,
                    value = if (isSelected) 8 else 5,
                    label = if (isSelected) "GREATEST" else "NEUTRAL",
                    isStrong = isSelected,
                    isWeak = false,
                    onClick = { onToggleStrength(trait) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${state.strongTraits.size} / 2 SELECTED",
            style = MaterialTheme.typography.labelMedium,
            color = if (state.strongTraits.size == 2) QuestGreen else BrutalMuted
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            enabled = state.canProceedToStep3,
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
                text = "CONTINUE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(1.dp, BrutalSubtle)
        ) {
            Text("BACK", color = BrutalMuted, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeaknessStep(
    state: CharacterCreationState,
    onToggleWeakness: (Trait) -> Unit,
    onBack: () -> Unit,
    onCreate: () -> Unit
) {
    val availableTraits = Trait.entries.filter { it !in state.strongTraits }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "UNTAPPED\nPOTENTIAL",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "PICK 2 TRAITS TO GROW INTO",
            style = MaterialTheme.typography.labelSmall,
            color = BrutalMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "THESE START AT 3 — QUESTS WILL PUSH YOU HERE",
            style = MaterialTheme.typography.labelSmall,
            color = DangerRed
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availableTraits.forEach { trait ->
                val isSelected = trait in state.weakTraits
                BrutalTraitCard(
                    trait = trait,
                    value = if (isSelected) 3 else 5,
                    label = if (isSelected) "UNTAPPED" else "NEUTRAL",
                    isStrong = false,
                    isWeak = isSelected,
                    onClick = { onToggleWeakness(trait) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${state.weakTraits.size} / 2 SELECTED",
            style = MaterialTheme.typography.labelMedium,
            color = if (state.weakTraits.size == 2) DangerRed else BrutalMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Character sheet summary
        HorizontalDivider(color = BrutalSubtle, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${state.name.uppercase()}'S CHARACTER SHEET",
            style = MaterialTheme.typography.labelMedium,
            color = BrutalMuted
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Trait.entries.forEach { trait ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = trait.symbol(),
                        fontSize = 12.sp,
                        color = when {
                            trait in state.strongTraits -> QuestGreen
                            trait in state.weakTraits -> DangerRed
                            else -> trait.color()
                        }
                    )
                    Text(
                        text = "${state.traitValue(trait)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = when {
                            trait in state.strongTraits -> QuestGreen
                            trait in state.weakTraits -> DangerRed
                            else -> Color.White
                        }
                    )
                    Text(
                        text = trait.displayName.take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onCreate,
            enabled = state.canCreate && !state.isCreating,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            if (state.isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "BEGIN YOUR ADVENTURE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(1.dp, BrutalSubtle)
        ) {
            Text("BACK", color = BrutalMuted, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BrutalTraitCard(
    trait: Trait,
    value: Int,
    label: String,
    isStrong: Boolean,
    isWeak: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isStrong -> QuestGreen
        isWeak -> DangerRed
        else -> trait.color().copy(alpha = 0.4f)
    }
    val containerColor = when {
        isStrong -> QuestGreen.copy(alpha = 0.08f)
        isWeak -> DangerRed.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 160.dp, height = 100.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = trait.symbol(),
                fontSize = 14.sp,
                color = borderColor
            )
            Text(
                text = trait.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = BrutalMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = borderColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = borderColor
            )
        }
    }
}
