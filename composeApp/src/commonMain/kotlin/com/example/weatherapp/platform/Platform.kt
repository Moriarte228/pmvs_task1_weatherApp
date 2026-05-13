package com.example.weatherapp.platform

/**
 * Текущая платформа выполнения. Определяется в платформенно-зависимых
 * реализациях actual.
 */
enum class Platform { Android, Ios, Desktop, Web }

expect val currentPlatform: Platform
