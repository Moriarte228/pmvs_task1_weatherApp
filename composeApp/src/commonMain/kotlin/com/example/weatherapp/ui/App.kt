package com.example.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.domain.WeatherData
import com.example.weatherapp.domain.weatherEmoji
import com.example.weatherapp.platform.Platform
import com.example.weatherapp.platform.currentPlatform

@Composable
fun App(viewModel: WeatherViewModel) {
    WeatherTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val state by viewModel.state.collectAsStateWithLifecycle()
            when (currentPlatform) {
                Platform.Android -> AndroidScreen(state, viewModel)
                Platform.Ios -> IosScreen(state, viewModel)
                Platform.Desktop -> DesktopScreen(state, viewModel)
                Platform.Web -> WebScreen(state, viewModel)
            }
        }
    }
}

/* -------------------- Android: Material 3, тени, скругления -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AndroidScreen(state: WeatherUiState, vm: WeatherViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Погода") },
                actions = {
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                placeholder = { Text("Введите город") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
            )
            Button(onClick = vm::addCity, modifier = Modifier.fillMaxWidth()) {
                Text("Добавить город")
            }
            LoadingOrError(state, vm)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(state.cities, key = { it.cityName }) { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        CityRow(data, onRemove = { vm.removeCity(data.cityName) })
                    }
                }
            }
        }
    }
}

/* -------------------- iOS: плоские карточки, SegmentedControl, SearchBar -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosScreen(state: WeatherUiState, vm: WeatherViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Погода") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // SearchBar-стиль: пилюля с иконкой
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = vm::onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск города") },
                    singleLine = true,
                )
            }
            Button(onClick = vm::addCity, modifier = Modifier.fillMaxWidth()) {
                Text("Добавить")
            }

            // Сегментированное переключение между городами
            if (state.cities.isNotEmpty()) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    state.cities.forEachIndexed { index, city ->
                        SegmentedButton(
                            selected = state.selectedCity == city.cityName,
                            onClick = { vm.selectCity(city.cityName) },
                            shape = SegmentedButtonDefaults.itemShape(index, state.cities.size),
                        ) { Text(city.cityName) }
                    }
                }
            }
            LoadingOrError(state, vm)

            val selected = state.cities.firstOrNull { it.cityName == state.selectedCity }
                ?: state.cities.firstOrNull()
            selected?.let {
                // Плоская карточка — без elevation, со светлой заливкой
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    WeatherDetailsBlock(it)
                }
            }
        }
    }
}

/* -------------------- Desktop (Linux): минималистично, чёткие рамки -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesktopScreen(state: WeatherUiState, vm: WeatherViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Погода", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.widthIn(min = 240.dp),
                placeholder = { Text("Город") },
                singleLine = true,
            )
            Button(onClick = vm::addCity) { Text("Добавить") }
            TextButton(onClick = vm::refresh) { Text("Обновить") }
        }
        LoadingOrError(state, vm)

        // Чёткие границы у элементов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.cities, key = { it.cityName }) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp),
                        ),
                ) {
                    CityRow(data, onRemove = { vm.removeCity(data.cityName) })
                }
            }
        }
    }
}

/* -------------------- Web: отзывчивая сетка (1/2/3 колонки) -------------------- */
@Composable
private fun WebScreen(state: WeatherUiState, vm: WeatherViewModel) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val columns = when {
            maxWidth < 600.dp -> 1
            maxWidth < 1024.dp -> 2
            else -> 3
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Погода", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = vm::onQueryChange,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    placeholder = { Text("Введите город") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                )
                Button(onClick = vm::addCity, modifier = Modifier.fillMaxWidth()) {
                    Text("Добавить")
                }
            }
            LoadingOrError(state, vm)
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.cities, key = { it.cityName }) { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        WeatherDetailsBlock(data)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { vm.removeCity(data.cityName) }) { Text("Удалить") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityRow(data: WeatherData, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* можно расширить для деталей */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(weatherEmoji(data.weatherId), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(data.cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(
                    data.description.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(formatTemp(data.temp), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = onRemove) { Text("×") }
        }
    }
}

@Composable
private fun LoadingOrError(state: WeatherUiState, vm: WeatherViewModel) {
    if (state.isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.height(24.dp).width(24.dp))
        }
    }
    state.errorMessage?.let { msg ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
            TextButton(onClick = vm::dismissError) { Text("OK") }
        }
    }
}
