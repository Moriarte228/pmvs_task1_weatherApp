package com.example.weatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.WeatherData
import com.example.weatherapp.domain.weatherEmoji

/**
 * Детальное представление погоды в карточке: температура, ощущается, влажность,
 * ветер, давление, видимость. Используется на всех платформах.
 */
@Composable
fun WeatherDetailsBlock(data: WeatherData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics { contentDescription = "weather-details-${data.cityName}" },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = data.cityName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = data.description.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
            Text(
                text = weatherEmoji(data.weatherId),
                style = MaterialTheme.typography.displayMedium,
            )
        }

        Text(
            text = formatTemp(data.temp),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DetailItem("Ощущается", formatTemp(data.feelsLike))
            DetailItem("Влажность", "${data.humidity}%")
            DetailItem("Ветер", "${formatDouble1(data.windSpeed)} м/с")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DetailItem("Давление", "${data.pressure} гПа")
            DetailItem("Видимость", "${data.visibility / 1000} км")
            Box(Modifier) // выравнивание под 3 колонки
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
    }
}

internal fun formatTemp(temp: Double): String {
    val rounded = (temp + if (temp >= 0) 0.5 else -0.5).toInt()
    return "${rounded}°"
}

internal fun formatDouble1(value: Double): String {
    val scaled = (value * 10 + if (value >= 0) 0.5 else -0.5).toInt()
    val whole = scaled / 10
    val frac = (if (scaled < 0) -scaled else scaled) % 10
    return "$whole.$frac"
}
