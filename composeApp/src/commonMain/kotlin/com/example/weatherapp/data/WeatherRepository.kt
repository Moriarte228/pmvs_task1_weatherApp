package com.example.weatherapp.data

import com.example.weatherapp.domain.WeatherData

/**
 * Объединяет сетевой запрос и кэш.
 * При успехе из сети — обновляет кэш.
 * При ошибке — отдаёт данные из кэша, если они есть.
 */
class WeatherRepository(
    private val api: WeatherApi,
    private val cache: WeatherCache,
) {

    suspend fun fetchWeather(city: String): Result<WeatherData> {
        val networkResult = api.getCurrentWeather(city)
        return networkResult.fold(
            onSuccess = { data ->
                cache.save(data)
                Result.success(data)
            },
            onFailure = { error ->
                val cached = cache.load(city)
                if (cached != null) Result.success(cached) else Result.failure(error)
            }
        )
    }

    fun loadCachedCities(): List<WeatherData> = cache.loadAll()

    fun removeCity(city: String) = cache.remove(city)
}
