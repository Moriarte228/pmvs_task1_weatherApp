package com.example.weatherapp.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun httpClientEngine(): HttpClientEngine = Darwin.create()

internal actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
