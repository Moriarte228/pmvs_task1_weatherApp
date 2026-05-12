package com.example.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val cities: List<WeatherData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val defaultCities = listOf("Минск", "Москва")

    init {
        defaultCities.forEach { loadWeather(it) }
    }

    fun loadWeather(city: String) {
        val trimmed = city.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getCurrentWeather(trimmed)
                .onSuccess { data ->
                    val updated = _uiState.value.cities.toMutableList()
                    val existingIndex = updated.indexOfFirst {
                        it.cityName.equals(data.cityName, ignoreCase = true)
                    }
                    if (existingIndex >= 0) updated[existingIndex] = data
                    else updated.add(data)
                    _uiState.value = _uiState.value.copy(
                        cities = updated,
                        isLoading = false
                    )
                }
                .onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Ошибка для $trimmed: ${err.message}"
                    )
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun removeCity(cityName: String) {
        val updated = _uiState.value.cities.filter {
            !it.cityName.equals(cityName, ignoreCase = true)
        }
        _uiState.value = _uiState.value.copy(cities = updated)
    }
}
