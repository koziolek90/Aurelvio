package com.kris.aurelvio.feature.assets.presentation

import com.kris.aurelvio.domain.model.Category

enum class AddAssetFormError {
    BLANK_NAME,
    NEGATIVE_INITIAL_VALUE,
    INVALID_INITIAL_VALUE,
}

data class AddAssetUiState(
    val name: String = "",
    val nameError: AddAssetFormError? = null,
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val selectedColorHex: String = AVAILABLE_COLORS.first(),
    val availableColors: List<String> = AVAILABLE_COLORS,
    val initialValueText: String = "",
    val initialValueError: AddAssetFormError? = null,
    val initialValueAsContribution: Boolean = true,
    val isLoadingCategories: Boolean = true,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean
        get() = name.isNotBlank() &&
            selectedCategory != null &&
            nameError == null &&
            initialValueError == null &&
            !isSaving

    companion object {
        val AVAILABLE_COLORS = listOf(
            "#4A90E2", // Blue
            "#7ED321", // Green
            "#F5A623", // Orange
            "#50E3C2", // Teal
            "#BD10E0", // Purple
            "#E65100", // Dark Orange
            "#D0021B", // Red
            "#90A4AE", // Grey
        )
    }
}

sealed interface AddAssetAction {
    data class NameChanged(val name: String) : AddAssetAction
    data class CategorySelected(val category: Category) : AddAssetAction
    data class ColorSelected(val colorHex: String) : AddAssetAction
    data class InitialValueChanged(val text: String) : AddAssetAction
    data class InitialValueAsContributionChanged(val isContribution: Boolean) : AddAssetAction
    data object SaveClicked : AddAssetAction
    data object BackClicked : AddAssetAction
}

sealed interface AddAssetEffect {
    data object NavigateBack : AddAssetEffect
    data class AssetCreated(val name: String) : AddAssetEffect
    data class ShowError(val message: String) : AddAssetEffect
}
