package com.example.weather

/**
 * Returns a platform-specific string identifying the current platform.
 */
expect fun getPlatformName(): String

/**
 * Platform-specific UI adaptations.
 */
expect fun isIOS(): Boolean
expect fun isDesktop(): Boolean
expect fun isWeb(): Boolean
