package com.pauls.lorebound.di

import com.pauls.lorebound.data.repository.CharacterRepositoryImpl
import com.pauls.lorebound.data.repository.FeatRepositoryImpl
import com.pauls.lorebound.data.repository.LoreRepositoryImpl
import com.pauls.lorebound.data.repository.QuestRepositoryImpl
import com.pauls.lorebound.data.repository.TitleRepositoryImpl
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.FeatRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.QuestRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindQuestRepository(impl: QuestRepositoryImpl): QuestRepository

    @Binds
    @Singleton
    abstract fun bindLoreRepository(impl: LoreRepositoryImpl): LoreRepository

    @Binds
    @Singleton
    abstract fun bindTitleRepository(impl: TitleRepositoryImpl): TitleRepository

    @Binds
    @Singleton
    abstract fun bindFeatRepository(impl: FeatRepositoryImpl): FeatRepository
}

