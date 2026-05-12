package com.example.weather

actual fun getPlatformName(): String = "iOS"
actual fun isIOS(): Boolean = true
actual fun isDesktop(): Boolean = false
actual fun isWeb(): Boolean = false
