package com.example.weatherapp.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun httpClientEngine(): HttpClientEngine = OkHttp.create()

internal actual fun nowMillis(): Long = System.currentTimeMillis()
