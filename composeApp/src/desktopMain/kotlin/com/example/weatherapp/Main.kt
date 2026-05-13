package com.example.weatherapp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.weatherapp.data.WeatherApiImpl
import com.example.weatherapp.data.WeatherCache
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.createHttpClient
import com.example.weatherapp.ui.App
import com.example.weatherapp.ui.WeatherViewModel
import com.russhwolf.settings.Settings

fun main() = application {
    val client = createHttpClient()
    val api = WeatherApiImpl(client)
    val cache = WeatherCache(Settings())
    val repository = WeatherRepository(api, cache)
    val viewModel = WeatherViewModel(repository)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Weather",
        state = rememberWindowState(width = 900.dp, height = 700.dp),
    ) {
        App(viewModel)
    }
}
