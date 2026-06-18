package com.pauls.lorebound.data.local.mapper

import com.pauls.lorebound.data.local.entity.ActiveQuestEntity
import com.pauls.lorebound.data.local.entity.CharacterEntity
import com.pauls.lorebound.data.local.entity.CompletedQuestEntity
import com.pauls.lorebound.data.local.entity.DailyQuestEntity
import com.pauls.lorebound.data.local.entity.FeatEntity
import com.pauls.lorebound.data.local.entity.LoreEntryEntity
import com.pauls.lorebound.data.local.entity.QuestEntity
import com.pauls.lorebound.data.local.entity.TitleEntity
import com.pauls.lorebound.domain.model.ActiveQuest
import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.DailyQuest
import com.pauls.lorebound.domain.model.Feat
import com.pauls.lorebound.domain.model.FeatRequirement
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestCategory
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Rarity
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.model.VerificationType

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    strength = strength,
    intelligence = intelligence,
    charisma = charisma,
    creativity = creativity,
    exploration = exploration,
    courage = courage,
    totalXp = totalXp,
    currentTitle = currentTitle,
    createdAt = createdAt,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate
)

fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    strength = strength,
    intelligence = intelligence,
    charisma = charisma,
    creativity = creativity,
    exploration = exploration,
    courage = courage,
    totalXp = totalXp,
    currentTitle = currentTitle,
    createdAt = createdAt,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate
)

fun QuestEntity.toDomain(): Quest = Quest(
    id = id,
    slug = slug,
    title = title,
    description = description,
    questType = runCatching { QuestType.valueOf(questType) }.getOrDefault(QuestType.DAILY),
    primaryAttribute = Trait.valueOf(primaryAttribute),
    secondaryAttribute = secondaryAttribute?.let { runCatching { Trait.valueOf(it) }.getOrNull() },
    xpReward = xpReward,
    difficulty = difficulty,
    estimatedMinutes = estimatedMinutes,
    durationDays = durationDays,
    storyWeight = storyWeight,
    rarity = runCatching { Rarity.valueOf(rarity) }.getOrDefault(Rarity.COMMON),
    verificationType = VerificationType.valueOf(verificationType),
    tags = tags.split(",").filter { it.isNotBlank() }.map { it.trim() }
)

fun Quest.toEntity(): QuestEntity = QuestEntity(
    id = id,
    slug = slug,
    title = title,
    description = description,
    questType = questType.name,
    primaryAttribute = primaryAttribute.name,
    secondaryAttribute = secondaryAttribute?.name,
    xpReward = xpReward,
    difficulty = difficulty,
    estimatedMinutes = estimatedMinutes,
    durationDays = durationDays,
    storyWeight = storyWeight,
    rarity = rarity.name,
    verificationType = verificationType.name,
    tags = tags.joinToString(",")
)

fun DailyQuestEntity.toDomain(): DailyQuest = DailyQuest(
    id = id,
    questId = questId,
    date = date,
    isCompleted = isCompleted
)

fun DailyQuest.toEntity(): DailyQuestEntity = DailyQuestEntity(
    id = id,
    questId = questId,
    date = date,
    isCompleted = isCompleted
)

fun ActiveQuestEntity.toDomain(): ActiveQuest = ActiveQuest(
    id = id,
    questId = questId,
    questType = QuestType.valueOf(questType),
    assignedDate = assignedDate,
    expiresDate = expiresDate,
    isCompleted = isCompleted,
    completedDate = completedDate
)

fun ActiveQuest.toEntity(): ActiveQuestEntity = ActiveQuestEntity(
    id = id,
    questId = questId,
    questType = questType.name,
    assignedDate = assignedDate,
    expiresDate = expiresDate,
    isCompleted = isCompleted,
    completedDate = completedDate
)

fun CompletedQuestEntity.toDomain(): CompletedQuest = CompletedQuest(
    id = id,
    questId = questId,
    completedAt = completedAt,
    verificationType = VerificationType.valueOf(verificationType),
    photoUri = photoUri,
    latitude = latitude,
    longitude = longitude
)

fun CompletedQuest.toEntity(): CompletedQuestEntity = CompletedQuestEntity(
    id = id,
    questId = questId,
    completedAt = completedAt,
    verificationType = verificationType.name,
    photoUri = photoUri,
    latitude = latitude,
    longitude = longitude
)

fun LoreEntryEntity.toDomain(): LoreEntry = LoreEntry(
    id = id,
    questId = questId,
    completedQuestId = completedQuestId,
    date = date,
    questTitle = questTitle,
    xpEarned = xpEarned,
    traitsImproved = traitsImproved.split(",").filter { it.isNotBlank() }.mapNotNull {
        runCatching { Trait.valueOf(it.trim()) }.getOrNull()
    },
    userNotes = userNotes,
    photoUri = photoUri,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    rankAtCompletion = rankAtCompletion,
    isPersonal = isPersonal,
    tags = tags.split(",").filter { it.isNotBlank() }.map { it.trim() },
    isFavorite = isFavorite,
    storyWeight = storyWeight
)

fun LoreEntry.toEntity(): LoreEntryEntity = LoreEntryEntity(
    id = id,
    questId = questId,
    completedQuestId = completedQuestId,
    date = date,
    questTitle = questTitle,
    xpEarned = xpEarned,
    traitsImproved = traitsImproved.joinToString(",") { it.name },
    userNotes = userNotes,
    photoUri = photoUri,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    rankAtCompletion = rankAtCompletion,
    isPersonal = isPersonal,
    tags = tags.joinToString(","),
    isFavorite = isFavorite,
    storyWeight = storyWeight
)

fun TitleEntity.toDomain(): Title = Title(
    id = id,
    name = name,
    description = description,
    category = QuestCategory.valueOf(category),
    requiredQuestCount = requiredQuestCount,
    requiredCategory = requiredCategory?.let { QuestCategory.valueOf(it) },
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

fun Title.toEntity(): TitleEntity = TitleEntity(
    id = id,
    name = name,
    description = description,
    category = category.name,
    requiredQuestCount = requiredQuestCount,
    requiredCategory = requiredCategory?.name,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

fun FeatEntity.toDomain(): Feat = Feat(
    id = id,
    name = name,
    description = description,
    requirement = FeatRequirement.valueOf(requirement),
    targetValue = targetValue,
    currentValue = currentValue,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

fun Feat.toEntity(): FeatEntity = FeatEntity(
    id = id,
    name = name,
    description = description,
    requirement = requirement.name,
    targetValue = targetValue,
    currentValue = currentValue,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

