package com.example.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val name: String,
    val main: MainData,
    val wind: WindData,
    val weather: List<WeatherCondition>,
    val visibility: Int = 0
)

@Serializable
data class MainData(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

@Serializable
data class WindData(
    val speed: Double
)

@Serializable
data class WeatherCondition(
    val id: Int,
    val description: String,
    val icon: String
)

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
    val weatherId: Int
)

fun WeatherResponse.toWeatherData() = WeatherData(
    cityName = name,
    temp = main.temp,
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    visibility = visibility,
    description = weather.firstOrNull()?.description ?: "",
    icon = weather.firstOrNull()?.icon ?: "",
    weatherId = weather.firstOrNull()?.id ?: 0
)

fun getWeatherEmoji(weatherId: Int): String = when {
    weatherId in 200..299 -> "⛈"
    weatherId in 300..399 -> "🌦"
    weatherId in 500..599 -> "🌧"
    weatherId in 600..699 -> "🌨"
    weatherId in 700..799 -> "🌫"
    weatherId == 800      -> "☀️"
    weatherId == 801      -> "🌤"
    weatherId == 802      -> "⛅"
    weatherId >= 803      -> "☁️"
    else                  -> "🌡"
}
