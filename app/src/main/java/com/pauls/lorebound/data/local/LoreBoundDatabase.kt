package com.pauls.lorebound.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pauls.lorebound.data.local.dao.CharacterDao
import com.pauls.lorebound.data.local.dao.FeatDao
import com.pauls.lorebound.data.local.dao.LoreEntryDao
import com.pauls.lorebound.data.local.dao.QuestDao
import com.pauls.lorebound.data.local.dao.TitleDao
import com.pauls.lorebound.data.local.entity.ActiveQuestEntity
import com.pauls.lorebound.data.local.entity.CharacterEntity
import com.pauls.lorebound.data.local.entity.CompletedQuestEntity
import com.pauls.lorebound.data.local.entity.DailyQuestEntity
import com.pauls.lorebound.data.local.entity.FeatEntity
import com.pauls.lorebound.data.local.entity.LoreEntryEntity
import com.pauls.lorebound.data.local.entity.QuestEntity
import com.pauls.lorebound.data.local.entity.TitleEntity

@Database(
    entities = [
        CharacterEntity::class,
        QuestEntity::class,
        DailyQuestEntity::class,
        CompletedQuestEntity::class,
        LoreEntryEntity::class,
        TitleEntity::class,
        FeatEntity::class,
        ActiveQuestEntity::class
    ],
    version = 6,
    exportSchema = true
)
abstract class LoreBoundDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun questDao(): QuestDao
    abstract fun loreEntryDao(): LoreEntryDao
    abstract fun titleDao(): TitleDao
    abstract fun featDao(): FeatDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Rename discipline column to courage in characters table
                db.execSQL("ALTER TABLE characters RENAME COLUMN discipline TO courage")
                // Migrate DISCIPLINE references in quest affectedStats
                db.execSQL("UPDATE quests SET affectedStats = REPLACE(affectedStats, 'DISCIPLINE', 'COURAGE')")
                // Migrate DISCIPLINE references in lore entries traitsImproved
                db.execSQL("UPDATE lore_entries SET traitsImproved = REPLACE(traitsImproved, 'DISCIPLINE', 'COURAGE')")
                // Migrate DISCIPLINE quest category references
                db.execSQL("UPDATE quests SET category = 'COURAGE' WHERE category = 'DISCIPLINE'")
                // Migrate title category references
                db.execSQL("UPDATE titles SET category = 'COURAGE' WHERE category = 'DISCIPLINE'")
                db.execSQL("UPDATE titles SET requiredCategory = 'COURAGE' WHERE requiredCategory = 'DISCIPLINE'")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new quests table with the updated schema
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS quests_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        slug TEXT NOT NULL DEFAULT '',
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        questType TEXT NOT NULL,
                        primaryAttribute TEXT NOT NULL DEFAULT 'COURAGE',
                        secondaryAttribute TEXT,
                        xpReward INTEGER NOT NULL,
                        difficulty INTEGER NOT NULL DEFAULT 1,
                        estimatedMinutes INTEGER NOT NULL,
                        durationDays INTEGER NOT NULL DEFAULT 1,
                        storyWeight INTEGER NOT NULL DEFAULT 1,
                        rarity TEXT NOT NULL DEFAULT 'COMMON',
                        verificationType TEXT NOT NULL,
                        tags TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())

                // Migrate existing data: map old category to primaryAttribute, old difficulty int stays
                db.execSQL("""
                    INSERT INTO quests_new (id, slug, title, description, questType, primaryAttribute, secondaryAttribute, xpReward, difficulty, estimatedMinutes, durationDays, storyWeight, rarity, verificationType, tags)
                    SELECT 
                        id,
                        LOWER(REPLACE(REPLACE(title, ' ', '_'), '''', '')) AS slug,
                        title,
                        description,
                        questType,
                        CASE category
                            WHEN 'EXPLORATION' THEN 'EXPLORATION'
                            WHEN 'LEARNING' THEN 'INTELLIGENCE'
                            WHEN 'FITNESS' THEN 'STRENGTH'
                            WHEN 'CREATIVITY' THEN 'CREATIVITY'
                            WHEN 'SOCIAL' THEN 'CHARISMA'
                            WHEN 'ADVENTURE' THEN 'EXPLORATION'
                            WHEN 'COURAGE' THEN 'COURAGE'
                            ELSE 'COURAGE'
                        END AS primaryAttribute,
                        NULL AS secondaryAttribute,
                        xpReward,
                        difficulty,
                        estimatedMinutes,
                        CASE questType
                            WHEN 'DAILY' THEN 1
                            WHEN 'SIDE_QUEST' THEN 7
                            WHEN 'ADVENTURE' THEN 30
                            WHEN 'EPIC' THEN 90
                            ELSE 1
                        END AS durationDays,
                        1 AS storyWeight,
                        CASE questType
                            WHEN 'DAILY' THEN 'COMMON'
                            WHEN 'SIDE_QUEST' THEN 'UNCOMMON'
                            WHEN 'ADVENTURE' THEN 'RARE'
                            WHEN 'EPIC' THEN 'EPIC'
                            ELSE 'COMMON'
                        END AS rarity,
                        verificationType,
                        '' AS tags
                    FROM quests
                """.trimIndent())

                // Drop old table and rename new
                db.execSQL("DROP TABLE quests")
                db.execSQL("ALTER TABLE quests_new RENAME TO quests")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add personal lore entry columns
                db.execSQL("ALTER TABLE lore_entries ADD COLUMN locationName TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE lore_entries ADD COLUMN isPersonal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE lore_entries ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE lore_entries ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE lore_entries ADD COLUMN storyWeight INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
