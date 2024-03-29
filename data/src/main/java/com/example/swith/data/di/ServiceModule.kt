package com.example.swith.data.di

import com.example.swith.data.api.SwithService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun provideSwithService(retrofit: Retrofit): SwithService {
        return retrofit.create(SwithService::class.java)
    }
}