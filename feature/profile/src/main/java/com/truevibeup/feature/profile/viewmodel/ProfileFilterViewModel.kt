package com.truevibeup.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.network.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

data class ProfileFilterState(
    val date: LocalDate = LocalDate.now(),
    val authorId: Long? = null,
    val posts: List<Post> = emptyList()
)

@HiltViewModel
class ProfileFilterViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileFilterState())
    val state: StateFlow<ProfileFilterState> = _state
}
