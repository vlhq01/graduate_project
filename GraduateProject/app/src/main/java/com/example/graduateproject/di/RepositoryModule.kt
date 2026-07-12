package com.example.graduateproject.di

import com.example.graduateproject.data.repository.AiChatRepositoryImpl
import com.example.graduateproject.data.repository.AuthRepositoryImpl
import com.example.graduateproject.data.repository.CategoriesRepositoryImpl
import com.example.graduateproject.data.repository.ProductRepositoryImpl
import com.example.graduateproject.data.repository.RecommendationRepositoryImpl
import com.example.graduateproject.data.repository.WorkspaceRepositoryImpl
import com.example.graduateproject.domain.repository.AiChatRepository
import com.example.graduateproject.domain.repository.AuthRepository
import com.example.graduateproject.domain.repository.CategoriesRepository
import com.example.graduateproject.domain.repository.ProductRepository
import com.example.graduateproject.domain.repository.RecommendationRepository
import com.example.graduateproject.domain.repository.WorkspaceRepository
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
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAiChatRepository(
        aiChatRepositoryImpl: AiChatRepositoryImpl
    ): AiChatRepository

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        recommendationRepositoryImpl: RecommendationRepositoryImpl
    ): RecommendationRepository

    @Binds
    @Singleton
    abstract fun bindWorkspaceRepository(
        workspaceRepositoryImpl: WorkspaceRepositoryImpl
    ): WorkspaceRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
