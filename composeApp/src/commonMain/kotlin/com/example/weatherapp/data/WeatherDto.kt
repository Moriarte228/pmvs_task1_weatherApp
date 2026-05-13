package com.example.weatherapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val name: String = "",
    val main: MainDto = MainDto(),
    val weather: List<WeatherInfoDto> = emptyList(),
    val wind: WindDto = WindDto(),
    val visibility: Int = 0,
)

@Serializable
data class MainDto(
    val temp: Double = 0.0,
    @SerialName("feels_like") val feelsLike: Double = 0.0,
    val humidity: Int = 0,
    val pressure: Int = 0,
)

@Serializable
data class WeatherInfoDto(
    val id: Int = 800,
    val description: String = "",
    val icon: String = "",
)

@Serializable
data class WindDto(
    val speed: Double = 0.0,
)
