package com.pauls.lorebound.di

import com.pauls.lorebound.AppConfig
import com.pauls.lorebound.domain.service.DebugTimeProvider
import com.pauls.lorebound.domain.service.LocalQuestGenerator
import com.pauls.lorebound.domain.service.QuestGenerator
import com.pauls.lorebound.domain.service.SystemTimeProvider
import com.pauls.lorebound.domain.service.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindQuestGenerator(impl: LocalQuestGenerator): QuestGenerator

    companion object {
        @Provides
        @Singleton
        fun provideTimeProvider(
            systemTimeProvider: SystemTimeProvider,
            debugTimeProvider: DebugTimeProvider
        ): TimeProvider {
            return if (AppConfig.isDeveloperMode) {
                debugTimeProvider
            } else {
                systemTimeProvider
            }
        }
    }
}
