package com.example.sunnxt_testapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sunnxt_testapplication.data.model.NavMenuItem
import com.example.sunnxt_testapplication.data.repository.NavMenuRepository
import com.example.sunnxt_testapplication.data.repository.NavMenuRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val menuItems: List<NavMenuItem>,
        val selectedActionUrl: String = "home"
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: NavMenuRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchNavMenu()
    }

    fun fetchNavMenu() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getNavMenu().fold(
                onSuccess = { items ->
                    _uiState.value = HomeUiState.Success(menuItems = items)
                },
                onFailure = { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun selectMenuItem(actionUrl: String) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(selectedActionUrl = actionUrl)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(NavMenuRepositoryImpl()) }
        }
    }
}
