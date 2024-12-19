package com.angelwong.infoweather.di

import com.angelwong.infoweather.data.repository.LocationRepository
import com.angelwong.infoweather.data.repository.SettingsRepository
import com.angelwong.infoweather.data.repository.WeatherRepository
import com.angelwong.infoweather.domain.repository.ILocationRepository
import com.angelwong.infoweather.domain.repository.ISettingsRepository
import com.angelwong.infoweather.domain.repository.IWeatherRepository
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
    abstract fun bindWeatherRepository(
        weatherRepository: WeatherRepository
    ): IWeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepository: LocationRepository
    ): ILocationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepository: SettingsRepository
    ): ISettingsRepository
}