package com.truevibeup.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.City
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.AuthRepository
import com.truevibeup.core.network.repository.LocationStore
import com.truevibeup.core.network.socket.SocketManager
import com.truevibeup.core.storage.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isNewUser: Boolean = false,
    val error: String? = null,
    // Store registration info during onboarding
    val registrationData: MutableMap<String, Any?> = mutableMapOf(),
    val countries: List<Country> = emptyList(),
    val cities: List<City> = emptyList(),
    val isLoadingLocations: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val locationStore: LocationStore,
    private val secureStorage: SecureStorage,
    private val socketManager: SocketManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        checkAuth()
        locationStore.loadCountries()
        viewModelScope.launch {
            locationStore.countries.collect { countries ->
                _state.value = _state.value.copy(countries = countries)
            }
        }
    }

    private fun checkAuth() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val loggedIn = secureStorage.isLoggedIn()
            if (loggedIn) {
                val user = secureStorage.getUser()
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    currentUser = user,
                )
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun loadCities(countryCode: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingLocations = true, cities = emptyList())
            val cities = locationStore.getCities(countryCode)
            _state.value = _state.value.copy(cities = cities, isLoadingLocations = false)
        }
    }

    fun deviceLogin() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, isNewUser = false)
            when (val result = authRepository.deviceLogin()) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = result.data,
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, isNewUser = true)
                }
            }
        }
    }

    fun onGoogleSignInSuccess(email: String, name: String?) {
        updateRegistrationData(mapOf(
            "email" to email,
            "name" to name,
            "auth_type" to "google"
        ))
    }

    fun updateRegistrationData(data: Map<String, Any?>) {
        val currentData = _state.value.registrationData.toMutableMap()
        currentData.putAll(data)
        _state.value = _state.value.copy(registrationData = currentData)
    }

    fun emailLogin(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.emailLogin(email, password)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = result.data,
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun completeOnboardingAndRegister() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val data = _state.value.registrationData
            val name = data["name"] as? String ?: ""
            val gender = data["gender"] as? String ?: "other"
            val birthday = data["birthday"] as? String
            val email = data["email"] as? String ?: ""
            val password = data["password"] as? String ?: ""

            val result = authRepository.emailRegister(
                email = email,
                password = password,
                name = name,
                gender = gender,
                birthday = birthday
            )

            when (result) {
                is ApiResult.Success -> {
                    val profileParams = data.filterKeys {
                        it !in listOf("name", "gender", "birthday", "email", "password", "auth_type")
                    }
                    if (profileParams.isNotEmpty()) {
                        authRepository.updateProfile(profileParams)
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = result.data,
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun logout() {
        viewModelScope.launch {
            socketManager.disconnect()
            authRepository.logout()
            _state.value = AuthState()
        }
    }

    fun updateProfile(params: Map<String, Any?>) {
        viewModelScope.launch {
            if (_state.value.isAuthenticated) {
                _state.value = _state.value.copy(isLoading = true)
                when (val result = authRepository.updateProfile(params)) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(isLoading = false, currentUser = result.data)
                    }
                    is ApiResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            } else {
                updateRegistrationData(params)
            }
        }
    }
}
