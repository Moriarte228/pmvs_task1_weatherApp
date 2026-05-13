package com.example.weatherapp.integration

import com.example.weatherapp.data.WeatherApiImpl
import com.example.weatherapp.testutil.SAMPLE_RAIN_JSON
import com.example.weatherapp.testutil.SAMPLE_WEATHER_JSON
import com.example.weatherapp.testutil.mockHttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WeatherApiIntegrationTest {

    @Test
    fun successful_response_is_parsed_into_domain_model() = runTest {
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_WEATHER_JSON), apiKey = "x")

        val result = api.getCurrentWeather("Moscow")

        assertTrue(result.isSuccess, "Expected success but was ${result.exceptionOrNull()}")
        val data = result.getOrNull()!!
        assertEquals("Moscow", data.cityName)
        assertEquals(12.3, data.temp)
        assertEquals(70, data.humidity)
        assertEquals(800, data.weatherId)
    }

    @Test
    fun rain_response_is_parsed_correctly() = runTest {
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_RAIN_JSON), apiKey = "x")

        val result = api.getCurrentWeather("Minsk")

        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(500, data.weatherId)
        assertEquals("дождь", data.description)
        assertEquals(6.0, data.windSpeed)
    }

    @Test
    fun http_error_is_returned_as_failure_result() = runTest {
        val api = WeatherApiImpl(
            mockHttpClient("not found", HttpStatusCode.NotFound),
            apiKey = "x",
        )

        val result = api.getCurrentWeather("Atlantis")

        assertFalse(result.isSuccess)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun api_does_not_throw_on_network_error() = runTest {
        val api = WeatherApiImpl(
            mockHttpClient("{}", HttpStatusCode.InternalServerError),
            apiKey = "x",
        )

        val result = api.getCurrentWeather("Anywhere")

        // Result-обёртка должна перехватить исключение
        assertFalse(result.isSuccess)
    }

    @Test
    fun timestamp_is_set_on_successful_response() = runTest {
        val api = WeatherApiImpl(mockHttpClient(SAMPLE_WEATHER_JSON), apiKey = "x")
        val result = api.getCurrentWeather("Moscow")
        assertTrue((result.getOrNull()?.timestamp ?: 0) > 0)
    }
}
