package com.tourly.app.home.presentation.state

import com.tourly.app.home.domain.model.Tour

data class GuideHomeUiState(
    val tours: List<Tour> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
