package com.example.weatherapp.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Платформенно-специфичный фабричный метод для создания движка Ktor.
 * Реализуется в каждом sourceSet (Android: OkHttp, iOS: Darwin, Desktop: CIO, Web: JS).
 */
expect fun httpClientEngine(): HttpClientEngine

/**
 * Создаёт настроенный HttpClient с JSON-сериализацией для kotlinx.serialization.
 */
fun createHttpClient(engine: HttpClientEngine = httpClientEngine()): HttpClient =
    HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            })
        }
    }
