package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.City
import com.truevibeup.core.common.model.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton store that caches location data globally.
 * Countries are loaded once at app start (Splash) and shared across all screens.
 * Cities are fetched on demand per country code and cached to avoid redundant API calls.
 */
@Singleton
class LocationStore @Inject constructor(
    private val locationRepository: LocationRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    private val _isLoadingCountries = MutableStateFlow(false)
    val isLoadingCountries: StateFlow<Boolean> = _isLoadingCountries.asStateFlow()

    // Cities cache keyed by country code
    private val citiesCache = mutableMapOf<String, List<City>>()

    /**
     * Load countries from API (no-op if already loaded or loading).
     * Called once at Splash via AuthViewModel.init.
     */
    fun loadCountries() {
        if (_countries.value.isNotEmpty() || _isLoadingCountries.value) return
        scope.launch {
            _isLoadingCountries.value = true
            when (val result = locationRepository.getCountries()) {
                is ApiResult.Success -> _countries.value = result.data
                is ApiResult.Error -> { /* keep empty, screens will degrade gracefully */ }
            }
            _isLoadingCountries.value = false
        }
    }

    /**
     * Fetch cities for a country code. Returns cached result if available,
     * otherwise calls the API and caches the result.
     */
    suspend fun getCities(countryCode: String): List<City> {
        citiesCache[countryCode]?.let { return it }
        return when (val result = locationRepository.getCities(countryCode)) {
            is ApiResult.Success -> {
                citiesCache[countryCode] = result.data
                result.data
            }
            is ApiResult.Error -> emptyList()
        }
    }
}
