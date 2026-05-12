package com.example.weather

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

// ─── Unit tests: WeatherData ───────────────────────────────────────────────

class WeatherDataTest {

    @Test
    fun testGetWeatherEmojiThunderstorm() {
        assertEquals("⛈", getWeatherEmoji(200))
        assertEquals("⛈", getWeatherEmoji(299))
    }

    @Test
    fun testGetWeatherEmojiRain() {
        assertEquals("🌧", getWeatherEmoji(500))
        assertEquals("🌧", getWeatherEmoji(531))
    }

    @Test
    fun testGetWeatherEmojiClear() {
        assertEquals("☀️", getWeatherEmoji(800))
    }

    @Test
    fun testGetWeatherEmojiCloudy() {
        assertEquals("☁️", getWeatherEmoji(804))
    }

    @Test
    fun testGetWeatherEmojiUnknown() {
        assertEquals("🌡", getWeatherEmoji(0))
    }

    @Test
    fun testWeatherResponseToWeatherData() {
        val response = WeatherResponse(
            name = "Минск",
            main = MainData(temp = 20.0, feelsLike = 18.0, humidity = 70, pressure = 1013),
            wind = WindData(speed = 3.5),
            weather = listOf(WeatherCondition(id = 800, description = "ясно", icon = "01d")),
            visibility = 10000
        )
        val data = response.toWeatherData()
        assertEquals("Минск", data.cityName)
        assertEquals(20.0, data.temp)
        assertEquals(70, data.humidity)
        assertEquals(3.5, data.windSpeed)
        assertEquals("ясно", data.description)
        assertEquals(800, data.weatherId)
    }

    @Test
    fun testWeatherResponseEmptyWeatherList() {
        val response = WeatherResponse(
            name = "Test",
            main = MainData(temp = 0.0, feelsLike = 0.0, humidity = 0, pressure = 0),
            wind = WindData(speed = 0.0),
            weather = emptyList()
        )
        val data = response.toWeatherData()
        assertEquals("", data.description)
        assertEquals(0, data.weatherId)
    }
}

// ─── Unit tests: Repository with mock HTTP client ─────────────────────────

val mockWeatherJson = """
{
  "name": "Москва",
  "main": {"temp": 15.0, "feels_like": 13.0, "humidity": 65, "pressure": 1012},
  "wind": {"speed": 4.0},
  "weather": [{"id": 801, "description": "небольшая облачность", "icon": "02d"}],
  "visibility": 9000
}
""".trimIndent()

fun createMockRepository(json: String, status: HttpStatusCode = HttpStatusCode.OK): WeatherRepository {
    val mockEngine = MockEngine { _ ->
        respond(
            content = json,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    val client = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    return WeatherRepositoryImpl(client)
}

class WeatherRepositoryTest {

    @Test
    fun testGetCurrentWeatherSuccess() = runTest {
        val repo = createMockRepository(mockWeatherJson)
        val result = repo.getCurrentWeather("Москва")
        assertTrue(result.isSuccess)
        val data = result.getOrThrow()
        assertEquals("Москва", data.cityName)
        assertEquals(15.0, data.temp)
        assertEquals(65, data.humidity)
    }

    @Test
    fun testGetCurrentWeatherCachesResult() = runTest {
        val repo = createMockRepository(mockWeatherJson)
        repo.getCurrentWeather("Москва")
        val cached = repo.getCachedWeather("Москва")
        assertNotNull(cached)
        assertEquals("Москва", cached.cityName)
    }

    @Test
    fun testGetCurrentWeatherNetworkError() = runTest {
        val repo = createMockRepository("{}", HttpStatusCode.InternalServerError)
        val result = repo.getCurrentWeather("НесуществующийГород")
        // Either failure or cached empty — must not throw
        assertNotNull(result)
    }

    @Test
    fun testGetAllCachedCitiesEmpty() {
        val repo = createMockRepository(mockWeatherJson)
        assertEquals(emptyList(), repo.getAllCachedCities())
    }

    @Test
    fun testCacheKeyIsCaseInsensitive() = runTest {
        val repo = createMockRepository(mockWeatherJson)
        repo.getCurrentWeather("Москва")
        val cachedLower = repo.getCachedWeather("москва")
        assertNotNull(cachedLower)
    }
}
