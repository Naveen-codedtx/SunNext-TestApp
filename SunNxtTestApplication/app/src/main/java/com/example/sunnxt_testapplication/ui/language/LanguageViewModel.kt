package com.example.sunnxt_testapplication.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sunnxt_testapplication.data.model.Language
import com.example.sunnxt_testapplication.data.repository.LanguageRepository
import com.example.sunnxt_testapplication.data.repository.LanguageRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LanguageUiState {
    data object Loading : LanguageUiState()
    data class Success(
        val languages: List<Language>,
        val selectedIds: Set<String> = emptySet()
    ) : LanguageUiState()
    data class Error(val message: String) : LanguageUiState()
}

class LanguageViewModel(
    private val repository: LanguageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LanguageUiState>(LanguageUiState.Loading)
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        fetchLanguages()
    }

    fun fetchLanguages() {
        viewModelScope.launch {
            _uiState.value = LanguageUiState.Loading
            repository.getLanguages()
                .onSuccess { languages ->
                    _uiState.value = LanguageUiState.Success(languages = languages)
                }
                .onFailure { error ->
                    _uiState.value = LanguageUiState.Error(
                        error.message ?: "Failed to load languages"
                    )
                }
        }
    }

    fun toggleLanguage(languageId: String) {
        val current = _uiState.value as? LanguageUiState.Success ?: return
        val updated = if (current.selectedIds.contains(languageId)) {
            current.selectedIds - languageId
        } else {
            current.selectedIds + languageId
        }
        _uiState.value = current.copy(selectedIds = updated)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LanguageViewModel(LanguageRepositoryImpl())
            }
        }
    }
}
