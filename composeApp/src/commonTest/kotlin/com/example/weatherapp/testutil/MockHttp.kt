package com.example.weatherapp.testutil

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

const val SAMPLE_WEATHER_JSON = """
{
  "name": "Moscow",
  "main": { "temp": 12.3, "feels_like": 10.1, "humidity": 70, "pressure": 1015 },
  "weather": [ { "id": 800, "description": "ясно", "icon": "01d" } ],
  "wind": { "speed": 3.5 },
  "visibility": 10000
}
"""

const val SAMPLE_RAIN_JSON = """
{
  "name": "Minsk",
  "main": { "temp": 5.0, "feels_like": 3.2, "humidity": 90, "pressure": 1003 },
  "weather": [ { "id": 500, "description": "дождь", "icon": "10d" } ],
  "wind": { "speed": 6.0 },
  "visibility": 8000
}
"""

/**
 * Создаёт HttpClient на базе MockEngine, всегда возвращающего указанный JSON.
 */
fun mockHttpClient(jsonBody: String, status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
    val engine = MockEngine { _ ->
        respond(
            content = ByteReadChannel(jsonBody),
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
    return HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
}
