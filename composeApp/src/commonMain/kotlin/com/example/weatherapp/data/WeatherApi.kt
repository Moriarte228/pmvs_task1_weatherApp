package com.example.weatherapp.data

import com.example.weatherapp.domain.WeatherData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Контракт работы с публичным API OpenWeatherMap.
 * Возвращает Result для безопасной обработки ошибок без исключений.
 */
interface WeatherApi {
    suspend fun getCurrentWeather(city: String): Result<WeatherData>
}

class WeatherApiImpl(
    private val httpClient: HttpClient,
    private val apiKey: String = DEFAULT_API_KEY,
) : WeatherApi {

    override suspend fun getCurrentWeather(city: String): Result<WeatherData> = runCatching {
        val response: WeatherResponseDto = httpClient
            .get("$BASE_URL/weather") {
                parameter("q", city)
                parameter("appid", apiKey)
                parameter("units", "metric")
                parameter("lang", "ru")
            }
            .body()

        val info = response.weather.firstOrNull()
        WeatherData(
            cityName = response.name.ifBlank { city },
            temp = response.main.temp,
            feelsLike = response.main.feelsLike,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            windSpeed = response.wind.speed,
            visibility = response.visibility,
            description = info?.description ?: "",
            icon = info?.icon ?: "",
            weatherId = info?.id ?: 800,
            timestamp = nowMillis(),
        )
    }

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5"
        const val DEFAULT_API_KEY = "9607e1be053088b89766797644fa2d32"
    }
}

internal expect fun nowMillis(): Long
