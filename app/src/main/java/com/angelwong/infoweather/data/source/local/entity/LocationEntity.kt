package com.angelwong.infoweather.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [Index(value = ["latitude", "longitude"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)