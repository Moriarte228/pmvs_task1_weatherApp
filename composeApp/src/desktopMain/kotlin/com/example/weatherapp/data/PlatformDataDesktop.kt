package com.example.weatherapp.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun httpClientEngine(): HttpClientEngine = CIO.create()

internal actual fun nowMillis(): Long = System.currentTimeMillis()
