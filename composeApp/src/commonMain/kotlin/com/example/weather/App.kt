package com.example.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun App(viewModel: WeatherViewModel = viewModel { WeatherViewModel() }) {
    MaterialTheme {
        WeatherScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val platform = getPlatformName()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌤 Погода", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            if (uiState.error != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) { Text(uiState.error ?: "") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search bar
            SearchInputRow(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = {
                    viewModel.loadWeather(uiState.searchQuery)
                    viewModel.updateSearchQuery("")
                },
                isIOS = isIOS()
            )

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            // Responsive grid based on platform / window size
            if (isWeb()) {
                ResponsiveGrid(
                    cities = uiState.cities,
                    onRemove = viewModel::removeCity
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.cities) { data ->
                        WeatherCard(data = data, onRemove = { viewModel.removeCity(data.cityName) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isIOS: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(if (isIOS) "Поиск города..." else "Введите город") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            },
            singleLine = true,
            shape = if (isIOS) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.medium
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onSearch,
            enabled = query.isNotBlank()
        ) {
            Text("Добавить")
        }
    }
}

@Composable
fun ResponsiveGrid(cities: List<WeatherData>, onRemove: (String) -> Unit) {
    BoxWithConstraints {
        val columns = when {
            maxWidth < 600.dp -> 1
            maxWidth < 900.dp -> 2
            else -> 3
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cities) { data ->
                WeatherCard(data = data, onRemove = { onRemove(data.cityName) })
            }
        }
    }
}

@Composable
fun WeatherCard(data: WeatherData, onRemove: () -> Unit) {
    val useShadow = !isIOS() && !isDesktop()
    val elevation = if (useShadow) 4.dp else 0.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isDesktop()) {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: emoji + city + temp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = getWeatherEmoji(data.weatherId),
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = data.cityName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = data.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${data.temp.toInt()}°C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ощущается ${data.feelsLike.toInt()}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(label = "Влажность", value = "${data.humidity}%")
                WeatherDetailItem(label = "Ветер", value = "${data.windSpeed} м/с")
                WeatherDetailItem(label = "Давление", value = "${data.pressure} гПа")
            }

            Spacer(Modifier.height(8.dp))

            // Remove button
            TextButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Удалить", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
