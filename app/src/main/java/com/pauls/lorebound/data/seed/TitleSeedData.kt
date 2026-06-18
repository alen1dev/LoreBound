package com.pauls.lorebound.data.seed

import com.pauls.lorebound.domain.model.QuestCategory
import com.pauls.lorebound.domain.model.Title

object TitleSeedData {

    fun getTitles(): List<Title> = listOf(
        // --- General progression ---
        Title(
            id = "first_steps",
            name = "First Steps",
            description = "Conquer your very first quest.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 1
        ),
        Title(
            id = "rising_adventurer",
            name = "Rising Adventurer",
            description = "Conquer 5 quests of any kind.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 5
        ),
        Title(
            id = "seasoned_adventurer",
            name = "Seasoned Adventurer",
            description = "Conquer 25 quests. The world is taking notice.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 25
        ),
        Title(
            id = "veteran_wanderer",
            name = "Veteran Wanderer",
            description = "Conquer 50 quests. Your legend grows.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 50
        ),
        Title(
            id = "legendary_hero",
            name = "Legendary Hero",
            description = "Conquer 100 quests. Songs are sung in your name.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 100
        ),

        // --- Exploration ---
        Title(
            id = "explorer_1",
            name = "Explorer I",
            description = "Conquer 3 exploration quests.",
            category = QuestCategory.EXPLORATION,
            requiredQuestCount = 3,
            requiredCategory = QuestCategory.EXPLORATION
        ),
        Title(
            id = "explorer_2",
            name = "Explorer II",
            description = "Conquer 10 exploration quests.",
            category = QuestCategory.EXPLORATION,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.EXPLORATION
        ),
        Title(
            id = "explorer_3",
            name = "Explorer III",
            description = "Conquer 25 exploration quests.",
            category = QuestCategory.EXPLORATION,
            requiredQuestCount = 25,
            requiredCategory = QuestCategory.EXPLORATION
        ),
        Title(
            id = "urban_wanderer",
            name = "Urban Wanderer",
            description = "Conquer 15 exploration quests. Every street tells a story.",
            category = QuestCategory.EXPLORATION,
            requiredQuestCount = 15,
            requiredCategory = QuestCategory.EXPLORATION
        ),

        // --- Learning ---
        Title(
            id = "curious_mind",
            name = "Curious Mind",
            description = "Conquer 3 learning quests.",
            category = QuestCategory.LEARNING,
            requiredQuestCount = 3,
            requiredCategory = QuestCategory.LEARNING
        ),
        Title(
            id = "museum_scholar",
            name = "Museum Scholar",
            description = "Conquer 5 learning quests.",
            category = QuestCategory.LEARNING,
            requiredQuestCount = 5,
            requiredCategory = QuestCategory.LEARNING
        ),
        Title(
            id = "book_hunter",
            name = "Book Hunter",
            description = "Conquer 10 learning quests.",
            category = QuestCategory.LEARNING,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.LEARNING
        ),

        // --- Social ---
        Title(
            id = "conversation_initiate",
            name = "Conversation Initiate",
            description = "Conquer 3 social quests.",
            category = QuestCategory.SOCIAL,
            requiredQuestCount = 3,
            requiredCategory = QuestCategory.SOCIAL
        ),
        Title(
            id = "conversation_adept",
            name = "Conversation Adept",
            description = "Conquer 10 social quests.",
            category = QuestCategory.SOCIAL,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.SOCIAL
        ),

        // --- Creativity ---
        Title(
            id = "creative_spark",
            name = "Creative Spark",
            description = "Conquer 3 creativity quests.",
            category = QuestCategory.CREATIVITY,
            requiredQuestCount = 3,
            requiredCategory = QuestCategory.CREATIVITY
        ),
        Title(
            id = "artisan",
            name = "Artisan",
            description = "Conquer 10 creativity quests.",
            category = QuestCategory.CREATIVITY,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.CREATIVITY
        ),

        // --- Fitness ---
        Title(
            id = "iron_will",
            name = "Iron Will",
            description = "Conquer 5 fitness quests.",
            category = QuestCategory.FITNESS,
            requiredQuestCount = 5,
            requiredCategory = QuestCategory.FITNESS
        ),
        Title(
            id = "unstoppable_force",
            name = "Unstoppable Force",
            description = "Conquer 15 fitness quests.",
            category = QuestCategory.FITNESS,
            requiredQuestCount = 15,
            requiredCategory = QuestCategory.FITNESS
        ),

        // --- Courage ---
        Title(
            id = "brave_soul",
            name = "Brave Soul",
            description = "Conquer 3 courage quests. You're stepping beyond.",
            category = QuestCategory.COURAGE,
            requiredQuestCount = 3,
            requiredCategory = QuestCategory.COURAGE
        ),
        Title(
            id = "risk_taker",
            name = "Risk Taker",
            description = "Conquer 10 courage quests. Fear bows to you.",
            category = QuestCategory.COURAGE,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.COURAGE
        ),
        Title(
            id = "fearless_wanderer",
            name = "Fearless Wanderer",
            description = "Conquer 15 courage quests. Nothing holds you back.",
            category = QuestCategory.COURAGE,
            requiredQuestCount = 15,
            requiredCategory = QuestCategory.COURAGE
        ),
        Title(
            id = "pathfinder",
            name = "Pathfinder",
            description = "Conquer 20 courage quests. You forge your own way.",
            category = QuestCategory.COURAGE,
            requiredQuestCount = 20,
            requiredCategory = QuestCategory.COURAGE
        ),
        Title(
            id = "bold_explorer",
            name = "Bold Explorer",
            description = "Conquer 30 courage quests. The unknown is your playground.",
            category = QuestCategory.COURAGE,
            requiredQuestCount = 30,
            requiredCategory = QuestCategory.COURAGE
        ),

        // --- Adventure ---
        Title(
            id = "adventure_seeker",
            name = "Adventure Seeker",
            description = "Conquer 5 adventure quests.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 5,
            requiredCategory = QuestCategory.ADVENTURE
        ),
        Title(
            id = "dawn_chaser",
            name = "Dawn Chaser",
            description = "Conquer 10 adventure quests. You chase the horizon.",
            category = QuestCategory.ADVENTURE,
            requiredQuestCount = 10,
            requiredCategory = QuestCategory.ADVENTURE
        )
    )
}
