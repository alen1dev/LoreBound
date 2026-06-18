package com.pauls.lorebound.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object CharacterCreation : Screen("character_creation")
    data object WelcomeSplash : Screen("welcome_splash")
    data object Home : Screen("home")
    data object QuestDetail : Screen("quest_detail/{questId}") {
        fun createRoute(questId: Long): String = "quest_detail/$questId"
    }
    data object LoreJournal : Screen("lore_journal")
    data object CreatePersonalLore : Screen("create_personal_lore")
    data object LoreEntryDetail : Screen("lore_entry_detail/{entryId}") {
        fun createRoute(entryId: Long): String = "lore_entry_detail/$entryId"
    }
    data object CharacterSheet : Screen("character_sheet")
    data object Titles : Screen("titles")
    data object Feats : Screen("feats")
    data object Settings : Screen("settings")
    data object StyleShowcase : Screen("style_showcase")
    data object DeveloperMenu : Screen("developer_menu")
    data object ChronicleExperience : Screen("chronicle_experience")
}

