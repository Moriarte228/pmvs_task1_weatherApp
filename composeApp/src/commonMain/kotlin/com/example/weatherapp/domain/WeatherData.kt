package com.example.weatherapp.domain

import kotlinx.serialization.Serializable

/**
 * Доменная модель погоды для одного города.
 * Хранит все ключевые показатели текущей погоды.
 */
@Serializable
data class WeatherData(
    val cityName: String,
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val visibility: Int,
    val description: String,
    val icon: String,
    val weatherId: Int,
    val timestamp: Long = 0L,
)

/**
 * Маппинг weatherId из OpenWeatherMap в emoji-иконку.
 */
fun weatherEmoji(weatherId: Int): String = when {
    weatherId in 200..299 -> "⛈"
    weatherId in 300..399 -> "🌦"
    weatherId in 500..599 -> "🌧"
    weatherId in 600..699 -> "🌨"
    weatherId in 700..799 -> "🌫"
    weatherId == 800 -> "☀️"
    weatherId == 801 -> "🌤"
    weatherId == 802 -> "⛅"
    weatherId >= 803 -> "☁️"
    else -> "🌡"
}
