package com.example.weatherapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.weatherapp.data.WeatherApiImpl
import com.example.weatherapp.data.WeatherCache
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.createHttpClient
import com.example.weatherapp.ui.App
import com.example.weatherapp.ui.WeatherViewModel
import com.russhwolf.settings.Settings

fun MainViewController() = ComposeUIViewController {
    val client = createHttpClient()
    val api = WeatherApiImpl(client)
    val cache = WeatherCache(Settings())
    val repository = WeatherRepository(api, cache)
    val viewModel = WeatherViewModel(repository)
    App(viewModel)
}
