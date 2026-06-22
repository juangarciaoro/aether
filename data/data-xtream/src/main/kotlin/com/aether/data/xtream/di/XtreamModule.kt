package com.aether.data.xtream.di

import com.aether.data.xtream.api.XtreamApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object XtreamModule {

    @Provides
    @Singleton
    fun providesXtreamApi(retrofit: Retrofit): XtreamApi =
        retrofit.create(XtreamApi::class.java)
}
