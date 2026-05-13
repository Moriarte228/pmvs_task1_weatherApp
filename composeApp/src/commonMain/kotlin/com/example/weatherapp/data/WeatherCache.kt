package com.example.weatherapp.data

import com.example.weatherapp.domain.WeatherData
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Простой ключ-значение кэш погодных данных для офлайн-просмотра.
 * Использует multiplatform-settings — единый API поверх SharedPreferences (Android),
 * NSUserDefaults (iOS), Properties (JVM), localStorage (Web).
 */
class WeatherCache(
    private val settings: Settings,
    private val json: Json = Json { ignoreUnknownKeys = true },
) {

    fun save(data: WeatherData) {
        settings.putString(keyFor(data.cityName), json.encodeToString(data))
        val cities = listCities().toMutableSet().apply { add(data.cityName) }
        settings.putString(CITIES_KEY, cities.joinToString(SEPARATOR))
    }

    fun load(city: String): WeatherData? {
        val raw = settings.getStringOrNull(keyFor(city)) ?: return null
        return runCatching { json.decodeFromString<WeatherData>(raw) }.getOrNull()
    }

    fun loadAll(): List<WeatherData> = listCities().mapNotNull { load(it) }

    fun listCities(): List<String> =
        settings.getStringOrNull(CITIES_KEY)
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    fun remove(city: String) {
        settings.remove(keyFor(city))
        val updated = listCities().filter { it != city }
        settings.putString(CITIES_KEY, updated.joinToString(SEPARATOR))
    }

    fun clear() {
        listCities().forEach { settings.remove(keyFor(it)) }
        settings.remove(CITIES_KEY)
    }

    private fun keyFor(city: String) = "$PREFIX$city"

    companion object {
        private const val PREFIX = "weather_"
        private const val CITIES_KEY = "cities_list"
        private const val SEPARATOR = "|"
    }
}
