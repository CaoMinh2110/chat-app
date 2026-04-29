package com.truevibeup.feature.profile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.City
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.AuthRepository
import com.truevibeup.core.network.repository.FeedRepository
import com.truevibeup.core.network.repository.LocationStore
import com.truevibeup.core.network.repository.UploadRepository
import com.truevibeup.core.network.util.UploadDiagnostics
import com.truevibeup.core.storage.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentUser: User? = null,
    val error: String? = null,
    val filterDate: LocalDate = LocalDate.now(),
    val filterAuthorId: Long? = null,
    val posts: List<Post> = emptyList(),
    val countries: List<Country> = emptyList(),
    val cities: List<City> = emptyList(),
    val isLoadingLocations: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val uploadError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val feedRepository: FeedRepository,
    private val locationStore: LocationStore,
    private val uploadRepository: UploadRepository,
    private val secureStorage: SecureStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private var currentPage = 1

    init {
        loadProfile()
        locationStore.loadCountries()
        viewModelScope.launch {
            locationStore.countries.collect { countries ->
                _state.value = _state.value.copy(countries = countries)
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            val hasData = _state.value.currentUser != null
            if (!hasData) _state.value = _state.value.copy(isLoading = true)
            val user = authRepository.getCurrentUser()
            _state.value = _state.value.copy(
                isLoading = false,
                currentUser = user ?: _state.value.currentUser
            )
            if (user != null && !hasData) {
                loadUserPosts(user.id, refresh = true)
            }
        }
    }

    fun loadUserPosts(uuid: String, refresh: Boolean = false) {
        if (refresh) currentPage = 1
        viewModelScope.launch {
            if (refresh) _state.value = _state.value.copy(isLoading = true)
            when (val result = feedRepository.getUserPosts(uuid, page = currentPage)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = if (refresh) result.data else _state.value.posts + result.data,
                        isLoading = false,
                        hasMore = result.data.size >= 20
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadMorePosts() {
        val uuid = _state.value.currentUser?.id ?: return
        if (_state.value.isLoadingMore || !_state.value.hasMore) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            when (val result = feedRepository.getUserPosts(uuid, page = ++currentPage)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = _state.value.posts + result.data,
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

    fun createPost(content: String, images: List<Uri>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val imageUrls = images.mapNotNull { uri ->
                val uploadResult = uploadRepository.uploadImage(uri)
                if (uploadResult.success) uploadResult.url else null
            }
            
            val finalContent = content.ifBlank { null }

            when (val result = feedRepository.createPost(finalContent, imageUrls)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = listOf(result.data) + _state.value.posts,
                        isLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.likePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.id == postId) it.copy(isLiked = true, likesCount = it.likesCount + 1) else it
                }
            )
        }
    }

    fun unlikePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.unlikePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.id == postId) it.copy(isLiked = false, likesCount = (it.likesCount - 1).coerceAtLeast(0)) else it
                }
            )
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.deletePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.filter { it.id != postId }
            )
        }
    }

    fun loadCities(countryCode: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingLocations = true, cities = emptyList())
            val cities = locationStore.getCities(countryCode)
            _state.value = _state.value.copy(cities = cities, isLoadingLocations = false)
        }
    }

    fun updateProfile(params: Map<String, Any?>, onBack: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = authRepository.updateProfile(params)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false, currentUser = result.data)
                    onBack()
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploadingAvatar = true, uploadError = null)
            
            try {
                val uploadResult = uploadRepository.uploadImage(uri)
                if (uploadResult.success && uploadResult.url != null) {
                    when (val result = authRepository.updateAvatar(uploadResult.url.toString())) {
                        is ApiResult.Success -> {
                            _state.value = _state.value.copy(
                                isUploadingAvatar = false,
                                currentUser = result.data,
                                uploadError = null
                            )
                        }
                        is ApiResult.Error -> {
                            _state.value = _state.value.copy(
                                isUploadingAvatar = false,
                                uploadError = "Failed to update profile: ${result.message}"
                            )
                        }
                    }
                } else {
                    val report = UploadDiagnostics.performDiagnostics(context, secureStorage)
                    Log.e("ProfileVM", report.toLogString())
                    
                    val detailedError = buildDetailedErrorMessage(
                        uploadResult.error ?: "Unknown error",
                        report
                    )
                    
                    _state.value = _state.value.copy(
                        isUploadingAvatar = false,
                        uploadError = detailedError
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Unexpected error during avatar upload", e)
                _state.value = _state.value.copy(
                    isUploadingAvatar = false,
                    uploadError = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    private fun buildDetailedErrorMessage(
        originalError: String,
        diagnostics: UploadDiagnostics.DiagnosticReport
    ): String {
        return when {
            !diagnostics.isLoggedIn -> {
                "Upload failed: Please login first and try again."
            }
            !diagnostics.isNetworkAvailable -> {
                "Upload failed: No internet connection. Please check your network."
            }
            !diagnostics.accessTokenExists -> {
                "Upload failed: Authentication token missing. Please logout and login again."
            }
            "404" in originalError -> {
                "Upload failed: Server endpoint not found. This might be a server issue. Please try again later."
            }
            else -> {
                "Upload failed: $originalError\n\nTroubleshooting: Please check your internet and try again."
            }
        }
    }
}
