package com.sudani.bat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudani.bat.data.model.SudaniNumber
import com.sudani.bat.data.repository.SudaniRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SudaniRepository) : ViewModel() {

    val allNumbers: StateFlow<List<SudaniNumber>> = repository.allNumbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshAll() {
        viewModelScope.launch {
            allNumbers.value.forEach { number ->
                repository.refreshDashboard(number)
            }
        }
    }

    fun claimAll() {
        viewModelScope.launch {
            allNumbers.value.forEach { number ->
                repository.claimPoints(number)
            }
        }
    }
}
