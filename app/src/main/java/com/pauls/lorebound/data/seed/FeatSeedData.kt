package com.pauls.lorebound.data.seed

import com.pauls.lorebound.domain.model.Feat
import com.pauls.lorebound.domain.model.FeatRequirement

object FeatSeedData {

    fun getFeats(): List<Feat> = listOf(
        Feat(
            id = "first_quest",
            name = "First Quest",
            description = "Conquer your very first quest.",
            requirement = FeatRequirement.QUESTS_COMPLETED,
            targetValue = 1
        ),
        Feat(
            id = "quests_10",
            name = "10 Quests Conquered",
            description = "Conquer 10 quests on your journey.",
            requirement = FeatRequirement.QUESTS_COMPLETED,
            targetValue = 10
        ),
        Feat(
            id = "quests_50",
            name = "50 Quests Conquered",
            description = "Conquer 50 quests and prove your dedication.",
            requirement = FeatRequirement.QUESTS_COMPLETED,
            targetValue = 50
        ),
        Feat(
            id = "quests_100",
            name = "100 Quests Conquered",
            description = "Conquer 100 quests. A true legend in the making.",
            requirement = FeatRequirement.QUESTS_COMPLETED,
            targetValue = 100
        ),
        Feat(
            id = "streak_7",
            name = "7 Day Streak",
            description = "Maintain a 7-day quest streak.",
            requirement = FeatRequirement.STREAK_DAYS,
            targetValue = 7
        ),
        Feat(
            id = "streak_30",
            name = "30 Day Streak",
            description = "Maintain a 30-day quest streak. Unwavering courage.",
            requirement = FeatRequirement.STREAK_DAYS,
            targetValue = 30
        ),
        Feat(
            id = "streak_100",
            name = "100 Day Streak",
            description = "Maintain a 100-day quest streak. Legendary commitment.",
            requirement = FeatRequirement.STREAK_DAYS,
            targetValue = 100
        ),
        Feat(
            id = "rank_5",
            name = "Reach Rank 5",
            description = "Achieve Rank 5 through your adventures.",
            requirement = FeatRequirement.RANK_REACHED,
            targetValue = 5
        ),
        Feat(
            id = "rank_10",
            name = "Reach Rank 10",
            description = "Achieve Rank 10. Your legend grows.",
            requirement = FeatRequirement.RANK_REACHED,
            targetValue = 10
        ),
        Feat(
            id = "rank_25",
            name = "Reach Rank 25",
            description = "Achieve Rank 25. The world knows your name.",
            requirement = FeatRequirement.RANK_REACHED,
            targetValue = 25
        ),
        Feat(
            id = "locations_10",
            name = "Visit 10 Locations",
            description = "Verify quests at 10 distinct locations.",
            requirement = FeatRequirement.LOCATIONS_VISITED,
            targetValue = 10
        ),
        Feat(
            id = "locations_50",
            name = "Visit 50 Locations",
            description = "Verify quests at 50 distinct locations. A true explorer.",
            requirement = FeatRequirement.LOCATIONS_VISITED,
            targetValue = 50
        ),
        Feat(
            id = "exploration_50",
            name = "50 Exploration Quests",
            description = "Complete 50 exploration quests.",
            requirement = FeatRequirement.CATEGORY_QUESTS_COMPLETED,
            targetValue = 50
        )
    )
}

