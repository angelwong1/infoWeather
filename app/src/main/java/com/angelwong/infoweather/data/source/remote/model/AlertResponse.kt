package com.angelwong.infoweather.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class AlertResponse(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("alerts")
    val alerts: List<Alert>?
) {
    data class Alert(
        @SerializedName("sender_name")
        val senderName: String,

        @SerializedName("event")
        val event: String,

        @SerializedName("start")
        val start: Long,

        @SerializedName("end")
        val end: Long,

        @SerializedName("description")
        val description: String,

        @SerializedName("tags")
        val tags: List<String>?
    )
}