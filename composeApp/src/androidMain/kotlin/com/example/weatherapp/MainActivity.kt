package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weatherapp.data.WeatherApiImpl
import com.example.weatherapp.data.WeatherCache
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.createHttpClient
import com.example.weatherapp.ui.App
import com.example.weatherapp.ui.WeatherViewModel
import com.russhwolf.settings.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val client = createHttpClient()
        val api = WeatherApiImpl(client)
        val cache = WeatherCache(Settings())
        val repository = WeatherRepository(api, cache)
        val viewModel = WeatherViewModel(repository)

        setContent { App(viewModel) }
    }
}
