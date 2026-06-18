package com.pauls.lorebound.ui.components

import androidx.compose.ui.graphics.Color
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Rarity
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.ui.theme.TraitStrength
import com.pauls.lorebound.ui.theme.TraitIntelligence
import com.pauls.lorebound.ui.theme.TraitCharisma
import com.pauls.lorebound.ui.theme.TraitCreativity
import com.pauls.lorebound.ui.theme.TraitExploration
import com.pauls.lorebound.ui.theme.TraitCourage

/** Unicode symbols for each trait — gives life to the stat grid */
fun Trait.symbol(): String = when (this) {
    Trait.STRENGTH -> "⚒"
    Trait.INTELLIGENCE -> "⬡"
    Trait.CHARISMA -> "♛"
    Trait.CREATIVITY -> "✺"
    Trait.EXPLORATION -> "⊛"
    Trait.COURAGE -> "△"
}

/** Per-trait accent color */
fun Trait.color(): Color = when (this) {
    Trait.STRENGTH -> TraitStrength
    Trait.INTELLIGENCE -> TraitIntelligence
    Trait.CHARISMA -> TraitCharisma
    Trait.CREATIVITY -> TraitCreativity
    Trait.EXPLORATION -> TraitExploration
    Trait.COURAGE -> TraitCourage
}

/** Short 3-letter code for each trait */
fun Trait.code(): String = when (this) {
    Trait.STRENGTH -> "STR"
    Trait.INTELLIGENCE -> "INT"
    Trait.CHARISMA -> "CHA"
    Trait.CREATIVITY -> "CRE"
    Trait.EXPLORATION -> "EXP"
    Trait.COURAGE -> "COU"
}

/** Difficulty symbols — ember bars that scale with intensity (1-5) */
fun difficultySymbol(level: Int): String = "▰".repeat(level.coerceIn(1, 5))

/** Quest type symbols */
fun QuestType.symbol(): String = when (this) {
    QuestType.DAILY -> "☀"
    QuestType.SIDE_QUEST -> "⬡"
    QuestType.ADVENTURE -> "⛰"
    QuestType.EPIC -> "⚜"
}

/** Rarity symbols */
fun Rarity.symbol(): String = when (this) {
    Rarity.COMMON -> "○"
    Rarity.UNCOMMON -> "◎"
    Rarity.RARE -> "◉"
    Rarity.EPIC -> "◆"
    Rarity.LEGENDARY -> "★"
}

/** Decorative rune divider text */
const val RUNE_DIVIDER = "— ◆ —"
const val RUNE_DIVIDER_WIDE = "── ◈ ── ◈ ──"
const val SECTION_DOT = "●"

