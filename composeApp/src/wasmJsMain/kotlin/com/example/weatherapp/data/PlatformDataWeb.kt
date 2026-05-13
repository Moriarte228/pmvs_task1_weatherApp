package com.example.weatherapp.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import kotlinx.datetime.Clock

actual fun httpClientEngine(): HttpClientEngine = Js.create()

internal actual fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()