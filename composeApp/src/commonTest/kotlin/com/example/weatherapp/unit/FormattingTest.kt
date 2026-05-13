package com.example.weatherapp.unit

import com.example.weatherapp.ui.formatDouble1
import com.example.weatherapp.ui.formatTemp
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattingTest {

    @Test
    fun positive_temp_is_rounded_up() {
        assertEquals("23°", formatTemp(22.6))
    }

    @Test
    fun negative_temp_is_rounded_away_from_zero() {
        assertEquals("-5°", formatTemp(-4.6))
    }

    @Test
    fun zero_temp_is_formatted_without_sign() {
        assertEquals("0°", formatTemp(0.0))
    }

    @Test
    fun double_is_formatted_with_one_decimal_place() {
        assertEquals("3.5", formatDouble1(3.45))
    }

    @Test
    fun double_with_round_value_keeps_zero_fraction() {
        assertEquals("4.0", formatDouble1(4.0))
    }
}
