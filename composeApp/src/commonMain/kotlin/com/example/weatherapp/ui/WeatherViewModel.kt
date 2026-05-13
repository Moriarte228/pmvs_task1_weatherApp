package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.domain.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val cities: List<WeatherData> = emptyList(),
    val selectedCity: String? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val defaultCities: List<String> = listOf("Minsk", "Moscow"),
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init {
        bootstrap()
    }

    private fun bootstrap() {
        val cached = repository.loadCachedCities()
        if (cached.isNotEmpty()) {
            _state.update { it.copy(cities = cached, selectedCity = cached.first().cityName) }
        }
        viewModelScope.launch {
            val cities = defaultCities + cached.map { it.cityName }
            cities.distinct().forEach { loadCity(it) }
        }
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun addCity() {
        val city = _state.value.query.trim()
        if (city.isEmpty()) return
        _state.update { it.copy(query = "") }
        viewModelScope.launch { loadCity(city) }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value.cities.forEach { loadCity(it.cityName) }
        }
    }

    fun selectCity(city: String) {
        _state.update { it.copy(selectedCity = city) }
    }

    fun removeCity(city: String) {
        repository.removeCity(city)
        _state.update { current ->
            val remaining = current.cities.filter { it.cityName != city }
            current.copy(
                cities = remaining,
                selectedCity = if (current.selectedCity == city) remaining.firstOrNull()?.cityName else current.selectedCity
            )
        }
    }

    fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private suspend fun loadCity(city: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        repository.fetchWeather(city).fold(
            onSuccess = { data ->
                _state.update { current ->
                    val updated = current.cities.filter { it.cityName != data.cityName } + data
                    current.copy(
                        cities = updated,
                        isLoading = false,
                        selectedCity = current.selectedCity ?: data.cityName
                    )
                }
            },
            onFailure = { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить $city: ${error.message ?: "ошибка сети"}"
                    )
                }
            }
        )
    }
}
