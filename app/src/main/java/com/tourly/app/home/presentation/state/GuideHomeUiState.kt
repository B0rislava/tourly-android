package com.tourly.app.home.presentation.state

import com.tourly.app.core.network.model.CreateTourResponseDto

data class GuideHomeUiState(
    val tours: List<CreateTourResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
