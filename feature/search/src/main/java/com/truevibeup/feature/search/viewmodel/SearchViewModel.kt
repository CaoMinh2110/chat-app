package com.truevibeup.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.AppConstants.MAX_AGE
import com.truevibeup.core.common.AppConstants.MIN_AGE
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.LocationStore
import com.truevibeup.core.network.repository.SearchRepository
import com.truevibeup.core.storage.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Int

data class SearchFilter(
    val country: String? = null,
    val ageMin: Int? = MIN_AGE,
    val ageMax: Int? = MAX_AGE,
    val gender: String? = null,
    val lookingFor: String? = null,
    val online: Boolean = false,
    val following: Boolean = false
)

fun SearchFilter.activeCount(default: SearchFilter): Int {
    var count = 0
    if (country != default.country) count++
    if (ageMin != default.ageMin) count++
    if (ageMax != default.ageMax) count++
    if (gender != default.gender) count++
    if (lookingFor != default.lookingFor) count++
    if (online != default.online) count++
    if (following != default.following) count++
    return count
}

data class SearchState(
    val query: String = "",
    val filters: SearchFilter = SearchFilter(),
    val defaultFilter: SearchFilter = SearchFilter(),
    val users: List<User> = emptyList(),
    val countries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val locationStore: LocationStore,
    private val secureStorage: SecureStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    private var searchJob: Job? = null
    private var currentPage = 1

    init {
        locationStore.loadCountries()
        viewModelScope.launch {
            locationStore.countries.collect { countries ->
                _state.value = _state.value.copy(countries = countries)
            }
        }
        viewModelScope.launch {
            val user = secureStorage.getUser()
            val defaultFilter = SearchFilter(
                country = user?.country,
                ageMin  = user?.lookingForAgeMin,
                ageMax  = user?.lookingForAgeMax,
                gender  = user?.lookingForGender,
            )
            _state.value = _state.value.copy(
                defaultFilter = defaultFilter,
                filters = defaultFilter
            )
            currentPage = 1
            performSearch()
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            currentPage = 1
            performSearch()
        }
    }

    fun applyFilter(filter: SearchFilter) {
        _state.value = _state.value.copy(filters = filter)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            currentPage = 1
            performSearch()
        }
    }

    private suspend fun performSearch() {
        val s = _state.value
        _state.value = s.copy(isLoading = true)
        val result = repository.searchUsers(
            query = s.query,
            page = currentPage,
            country = s.filters.country,
            ageMin = s.filters.ageMin,
            ageMax = s.filters.ageMax,
            gender = s.filters.gender,
        )
        when (result) {
            is ApiResult.Success -> {
                _state.value = _state.value.copy(
                    users = result.data,
                    isLoading = false,
                    hasMore = result.data.size >= 20
                )
            }
            is ApiResult.Error -> {
                _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.hasMore) return
        currentPage++
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            val s = _state.value
            val result = repository.searchUsers(
                query = s.query,
                page = currentPage,
                country = s.filters.country,
                ageMin = s.filters.ageMin,
                ageMax = s.filters.ageMax,
                gender = s.filters.gender,
            )
            when (result) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        users = _state.value.users + result.data,
                        isLoadingMore = false,
                        hasMore = result.data.size >= 20
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoadingMore = false)
                }
            }
        }
    }

    fun getOrCreateConversation(userId: String, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            when (val result = repository.getOrCreateConversation(userId)) {
                is ApiResult.Success -> onSuccess(result.data.id)
                else -> {}
            }
        }
    }
}
