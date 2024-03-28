package com.example.cahier.ui

import androidx.lifecycle.ViewModel
import com.example.cahier.data.HomePaneUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomePaneViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomePaneUiState())
    val uiState: StateFlow<HomePaneUiState> = _uiState.asStateFlow()
}