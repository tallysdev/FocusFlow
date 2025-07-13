package com.example.focusflow.di

import android.content.Context
import com.example.focusflow.data.repository.ActivityRepository
import com.example.focusflow.data.repository.SubActivityRepository
import com.example.focusflow.data.repository.SubtaskGeneratorRepository
import com.example.focusflow.data.repository.TrophiesRepository
import com.example.focusflow.data.repository.UserRepository
import com.example.focusflow.domain.repository.IActivityRepository
import com.example.focusflow.domain.repository.ISubActivityRepository
import com.example.focusflow.domain.repository.ISubtaskGeneratorRepository
import com.example.focusflow.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepository: UserRepository): IUserRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(activityRepository: ActivityRepository): IActivityRepository

    @Binds
    @Singleton
    abstract fun bindSubActivityRepository(subActivityRepository: SubActivityRepository): ISubActivityRepository

    @Binds
    @Singleton
    abstract fun bindSubtaskGeneratorRepository(subtaskGeneratorRepository: SubtaskGeneratorRepository): ISubtaskGeneratorRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTrophiesRepository(
        @ApplicationContext context: Context,
    ): TrophiesRepository = TrophiesRepository(context)
}
