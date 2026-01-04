package com.tourly.app.create_tour.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.tourly.app.create_tour.domain.TourType
import com.tourly.app.create_tour.domain.usecase.CreateTourUseCase
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.state.DayProgramme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateTourViewModel @Inject constructor(
    private val createTourUseCase: CreateTourUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTourUiState(
        days = listOf(DayProgramme(1, ""), DayProgramme(2, "")) // Default 2 days
    ))
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

    fun onTourTypeChanged(type: TourType) {
        _uiState.update {
            it.copy(type = type)
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDurationChanged(duration: String) {
        val isValid = if (_uiState.value.type == TourType.SINGLE_DAY) {
            // Regex for positive integer or decimal (e.g., "3", "3.5")
            // Empty allowed while typing
            duration.isEmpty() || duration.matches(Regex("^\\d*\\.?\\d*$"))
        } else {
            // For Multi-Day, user might type manually, standard is integer
            duration.isEmpty() || duration.all { char -> char.isDigit() }
        }

        if (isValid) {
            _uiState.update { it.copy(duration = duration) }
            
            // Sync days if Multi-Day and valid number
            if (_uiState.value.type == TourType.MULTI_DAY && duration.isNotEmpty()) {
                onTotalDaysChanged(duration)
            }
        }
    }

    fun onLocationChanged(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun onMaxGroupSizeChanged(sizeStr: String) {
         // Allow only numbers
         if (sizeStr.isEmpty() || sizeStr.all { it.isDigit() }) {
             _uiState.update { 
                 it.copy(maxGroupSize = sizeStr.toIntOrNull() ?: 0) 
             }
         }
    }

    fun onPriceChanged(price: Double) {
        _uiState.update { it.copy(pricePerPerson = price) }
    }

    fun onWhatsIncludedChanged(included: String) {
        _uiState.update { it.copy(whatsIncluded = included) }
    }


    fun onAddDay() {
        _uiState.update { currentState ->
            val currentDays = currentState.days ?: emptyList()
            val newDays = currentDays.toMutableList().apply {
                add(DayProgramme(dayNumber = size + 1, description = ""))
            }
            currentState.copy(
                days = newDays,
                duration = newDays.size.toString()
            )
        }
    }

    fun onRemoveDay(index: Int) {
        _uiState.update { currentState ->
            val currentDays = currentState.days ?: emptyList()
            if (currentDays.size > 2) {
                val newDays = currentDays.toMutableList().apply {
                   removeAt(index)
                }
                // Renumber days
                val renumberedDays = newDays.mapIndexed { i, day ->
                    day.copy(dayNumber = i + 1)
                }
                currentState.copy(
                    days = renumberedDays,
                    duration = renumberedDays.size.toString()
                )
            } else {
                currentState
            }
        }
    }

    fun onDayDescriptionChanged(index: Int, description: String) {
        _uiState.update { currentState ->
            val currentDays = currentState.days ?: emptyList()
            if (index in currentDays.indices) {
                val newDays = currentDays.toMutableList().apply {
                    this[index] = this[index].copy(description = description)
                }
                currentState.copy(days = newDays)
            } else {
                currentState
            }
        }
    }

    private fun onTotalDaysChanged(totalDaysStr: String) {
        val totalDays = totalDaysStr.toIntOrNull()
        if (totalDays != null && totalDays >= 2) {
             _uiState.update { currentState ->
                 val currentDays = currentState.days ?: emptyList()
                 val newDays = currentDays.toMutableList()
                 
                 if (totalDays > currentDays.size) {
                     repeat(totalDays - currentDays.size) {
                         newDays.add(DayProgramme(dayNumber = newDays.size + 1, description = ""))
                     }
                 } else if (totalDays < currentDays.size) {
                     repeat(currentDays.size - totalDays) {
                         newDays.removeAt(newDays.lastIndex)
                     }
                 }
                 
                 currentState.copy(days = newDays)
             }
        }
    }
}