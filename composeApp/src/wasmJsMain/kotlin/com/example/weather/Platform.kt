package com.example.weather

actual fun getPlatformName(): String = "Web"
actual fun isIOS(): Boolean = false
actual fun isDesktop(): Boolean = false
actual fun isWeb(): Boolean = true
