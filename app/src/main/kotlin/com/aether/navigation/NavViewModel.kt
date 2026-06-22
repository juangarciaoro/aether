package com.aether.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
) : ViewModel() {

    val onboardingComplete = prefsRepository.onboardingComplete

    fun setOnboardingComplete() {
        viewModelScope.launch {
            prefsRepository.setOnboardingComplete(true)
        }
    }
}
