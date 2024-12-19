package com.angelwong.infoweather.domain.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("es"))
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale("es"))
    private val dayFormatter = SimpleDateFormat("EEEE", Locale("es"))
    private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es"))

    fun formatDate(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp * 1000))
    }

    fun formatDayOfWeek(timestamp: Long): String {
        return dayFormatter.format(Date(timestamp))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }
    }
}