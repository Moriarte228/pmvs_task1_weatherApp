package com.example.weatherapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.example.weatherapp.domain.WeatherData
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class WeatherDetailsBlockTest {

    private val sample = WeatherData(
        cityName = "Moscow",
        temp = 12.3,
        feelsLike = 10.1,
        humidity = 70,
        pressure = 1015,
        windSpeed = 3.5,
        visibility = 10000,
        description = "ясно",
        icon = "01d",
        weatherId = 800,
        timestamp = 1L,
    )

    @Test
    fun shows_city_name() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithText("Moscow").assertIsDisplayed()
    }

    @Test
    fun shows_temperature() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithText("12°").assertIsDisplayed()
    }

    @Test
    fun shows_humidity_value() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithText("70%").assertIsDisplayed()
    }

    @Test
    fun shows_wind_speed_value() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithText("3.5 м/с").assertIsDisplayed()
    }

    @Test
    fun shows_capitalized_description() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithText("Ясно").assertIsDisplayed()
    }

    @Test
    fun applies_semantics_tag_for_block() = runComposeUiTest {
        setContent { WeatherDetailsBlock(sample) }
        onNodeWithContentDescription("weather-details-Moscow").assertIsDisplayed()
    }
}
