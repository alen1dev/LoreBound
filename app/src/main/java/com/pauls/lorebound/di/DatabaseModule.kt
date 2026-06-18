package com.pauls.lorebound.di

import android.content.Context
import androidx.room.Room
import com.pauls.lorebound.data.local.LoreBoundDatabase
import com.pauls.lorebound.data.local.dao.CharacterDao
import com.pauls.lorebound.data.local.dao.FeatDao
import com.pauls.lorebound.data.local.dao.LoreEntryDao
import com.pauls.lorebound.data.local.dao.QuestDao
import com.pauls.lorebound.data.local.dao.TitleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LoreBoundDatabase {
        return Room.databaseBuilder(
            context,
            LoreBoundDatabase::class.java,
            "lorebound.db"
        )
            .addMigrations(
                LoreBoundDatabase.MIGRATION_3_4,
                LoreBoundDatabase.MIGRATION_4_5,
                LoreBoundDatabase.MIGRATION_5_6
            )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideCharacterDao(database: LoreBoundDatabase): CharacterDao =
        database.characterDao()

    @Provides
    fun provideQuestDao(database: LoreBoundDatabase): QuestDao =
        database.questDao()

    @Provides
    fun provideLoreEntryDao(database: LoreBoundDatabase): LoreEntryDao =
        database.loreEntryDao()

    @Provides
    fun provideTitleDao(database: LoreBoundDatabase): TitleDao =
        database.titleDao()

    @Provides
    fun provideFeatDao(database: LoreBoundDatabase): FeatDao =
        database.featDao()
}

