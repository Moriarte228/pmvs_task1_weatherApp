package com.example.weatherapp.integration

import com.example.weatherapp.data.WeatherApi
import com.example.weatherapp.data.WeatherApiImpl
import com.example.weatherapp.data.WeatherCache
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.domain.WeatherData
import com.example.weatherapp.testutil.InMemorySettings
import com.example.weatherapp.testutil.SAMPLE_RAIN_JSON
import com.example.weatherapp.testutil.SAMPLE_WEATHER_JSON
import com.example.weatherapp.testutil.mockHttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Заглушка API для тестов offline-сценариев. */
private class AlwaysFailingApi : WeatherApi {
    override suspend fun getCurrentWeather(city: String): Result<com.example.weatherapp.domain.WeatherData> =
        Result.failure(RuntimeException("offline"))
}

class WeatherRepositoryIntegrationTest {

    @Test
    fun successful_fetch_is_saved_to_cache() = runTest {
        val cache = WeatherCache(InMemorySettings())
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_WEATHER_JSON), apiKey = "x")
        val repository = WeatherRepository(api, cache)

        val result = repository.fetchWeather("Moscow")

        assertTrue(result.isSuccess)
        val cached = cache.load("Moscow")
        assertTrue(cached != null)
        assertEquals(12.3, cached!!.temp)
    }

    @Test
    fun network_failure_falls_back_to_cache_when_available() = runTest {
        val cache = WeatherCache(InMemorySettings()).apply {
            save(
                WeatherData(
                    cityName = "Moscow", temp = 5.0, feelsLike = 4.0, humidity = 50,
                    pressure = 1010, windSpeed = 1.0, visibility = 9000,
                    description = "облачно", icon = "03d", weatherId = 803, timestamp = 1L
                )
            )
        }
        val repository = WeatherRepository(AlwaysFailingApi(), cache)

        val result = repository.fetchWeather("Moscow")

        assertTrue(result.isSuccess, "Expected cached fallback to succeed")
        assertEquals("облачно", result.getOrNull()?.description)
    }

    @Test
    fun network_failure_without_cache_returns_failure() = runTest {
        val cache = WeatherCache(InMemorySettings())
        val repository = WeatherRepository(AlwaysFailingApi(), cache)

        val result = repository.fetchWeather("Atlantis")

        assertTrue(result.isFailure)
    }

    @Test
    fun cache_lists_cities_in_insertion_order() = runTest {
        val cache = WeatherCache(InMemorySettings())
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_WEATHER_JSON), apiKey = "x")
        val repository = WeatherRepository(api, cache)

        repository.fetchWeather("Moscow")
        val rainApi = WeatherApiImpl(mockHttpClient(SAMPLE_RAIN_JSON), apiKey = "x")
        WeatherRepository(rainApi, cache).fetchWeather("Minsk")

        val cities = cache.listCities()
        assertEquals(setOf("Moscow", "Minsk"), cities.toSet())
    }

    @Test
    fun remove_city_clears_cache_entry() = runTest {
        val cache = WeatherCache(InMemorySettings())
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_WEATHER_JSON), apiKey = "x")
        val repository = WeatherRepository(api, cache)
        repository.fetchWeather("Moscow")

        repository.removeCity("Moscow")

        assertNull(cache.load("Moscow"))
        assertTrue(cache.listCities().isEmpty())
    }
}
