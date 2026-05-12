package com.example.weather

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val API_KEY = "9607e1be053088b89766797644fa2d32"
const val BASE_URL = "https://api.openweathermap.org/data/2.5"

interface WeatherRepository {
    suspend fun getCurrentWeather(city: String): Result<WeatherData>
    fun getCachedWeather(city: String): WeatherData?
    fun getAllCachedCities(): List<String>
}

class WeatherRepositoryImpl(
    private val httpClient: HttpClient = createHttpClient()
) : WeatherRepository {

    // In-memory cache: city (lowercase) -> WeatherData
    private val cache = mutableMapOf<String, WeatherData>()

    override suspend fun getCurrentWeather(city: String): Result<WeatherData> {
        return try {
            val response: WeatherResponse = httpClient.get("$BASE_URL/weather") {
                parameter("q", city)
                parameter("appid", API_KEY)
                parameter("units", "metric")
                parameter("lang", "ru")
            }.body()
            val data = response.toWeatherData()
            cache[city.lowercase()] = data
            Result.success(data)
        } catch (e: Exception) {
            // Try returning cached value on error
            val cached = cache[city.lowercase()]
            if (cached != null) Result.success(cached)
            else Result.failure(e)
        }
    }

    override fun getCachedWeather(city: String): WeatherData? =
        cache[city.lowercase()]

    override fun getAllCachedCities(): List<String> =
        cache.keys.toList()
}

fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}
