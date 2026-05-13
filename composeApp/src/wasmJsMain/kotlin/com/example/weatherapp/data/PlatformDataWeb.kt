package com.example.weatherapp.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import kotlin.js.Date

actual fun httpClientEngine(): HttpClientEngine = Js.create()

internal actual fun nowMillis(): Long = Date().getTime().toLong()
