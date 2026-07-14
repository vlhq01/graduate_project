package com.example.graduateproject.di

import com.example.graduateproject.data.remote.AiChatApiService
import com.example.graduateproject.data.remote.AuthApiService
import com.example.graduateproject.data.remote.AuthInterceptor
import com.example.graduateproject.data.remote.CategoriesApiService
import com.example.graduateproject.data.remote.NotificationApiService
import com.example.graduateproject.data.remote.ProductApiService
import com.example.graduateproject.data.remote.RecommendationApiService
import com.example.graduateproject.data.remote.WorkspaceApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = " https://techadvisor.dpdns.org/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(firebaseAuth: FirebaseAuth): AuthInterceptor {
        return AuthInterceptor(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val networkJson = Json { ignoreUnknownKeys = true } // Bỏ qua field thừa từ backend
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAiChatApiService(retrofit: Retrofit): AiChatApiService {
        return retrofit.create(AiChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkspaceApiService(retrofit: Retrofit): WorkspaceApiService {
        return retrofit.create(WorkspaceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideRecommendationApiService(retrofit: Retrofit): RecommendationApiService {
        return retrofit.create(RecommendationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoriesApiService(retrofit: Retrofit): CategoriesApiService {
        return retrofit.create(CategoriesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}