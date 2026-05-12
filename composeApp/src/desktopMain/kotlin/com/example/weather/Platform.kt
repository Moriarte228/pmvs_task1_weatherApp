package com.example.weather

actual fun getPlatformName(): String = "Desktop"
actual fun isIOS(): Boolean = false
actual fun isDesktop(): Boolean = true
actual fun isWeb(): Boolean = false
