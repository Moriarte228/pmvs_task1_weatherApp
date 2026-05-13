package com.example.weatherapp.unit

import com.example.weatherapp.domain.weatherEmoji
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherEmojiTest {

    @Test
    fun thunderstorm_returns_thunder_emoji() {
        assertEquals("⛈", weatherEmoji(211))
    }

    @Test
    fun clear_sky_returns_sun_emoji() {
        assertEquals("☀️", weatherEmoji(800))
    }

    @Test
    fun heavy_clouds_return_cloud_emoji() {
        assertEquals("☁️", weatherEmoji(804))
    }

    @Test
    fun snow_returns_snow_emoji() {
        assertEquals("🌨", weatherEmoji(601))
    }

    @Test
    fun unknown_id_returns_thermometer_fallback() {
        assertEquals("🌡", weatherEmoji(-1))
    }

    @Test
    fun rain_range_returns_rain_emoji() {
        assertEquals("🌧", weatherEmoji(500))
        assertEquals("🌧", weatherEmoji(531))
    }
}
