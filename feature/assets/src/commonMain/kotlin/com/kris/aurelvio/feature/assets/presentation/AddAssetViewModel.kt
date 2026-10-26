package com.kris.aurelvio.feature.assets.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kris.aurelvio.core.common.result.Outcome
import com.kris.aurelvio.domain.model.AssetDraft
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.domain.usecase.AddAssetError
import com.kris.aurelvio.domain.usecase.AddAssetUseCase
import com.kris.aurelvio.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddAssetViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addAssetUseCase: AddAssetUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddAssetUiState())
    val state: StateFlow<AddAssetUiState> = _state.asStateFlow()

    private val _effects = Channel<AddAssetEffect>(Channel.BUFFERED)
    val effects: Flow<AddAssetEffect> = _effects.receiveAsFlow()

    init {
        loadCategories()
    }

    fun onAction(action: AddAssetAction) {
        when (action) {
            is AddAssetAction.NameChanged -> handleNameChanged(action.name)
            is AddAssetAction.CategorySelected -> handleCategorySelected(action)
            is AddAssetAction.ColorSelected -> handleColorSelected(action.colorHex)
            is AddAssetAction.InitialValueChanged -> handleInitialValueChanged(action.text)
            is AddAssetAction.InitialValueAsContributionChanged -> {
                _state.update { it.copy(initialValueAsContribution = action.isContribution) }
            }
            AddAssetAction.SaveClicked -> saveAsset()
            AddAssetAction.BackClicked -> _effects.trySend(AddAssetEffect.NavigateBack)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categoryList ->
                _state.update { currentState ->
                    currentState.copy(
                        categories = categoryList,
                        selectedCategory = currentState.selectedCategory ?: categoryList.firstOrNull(),
                        isLoadingCategories = false,
                    )
                }
            }
        }
    }

    private fun handleNameChanged(rawName: String) {
        _state.update { currentState ->
            currentState.copy(
                name = rawName,
                nameError = if (rawName.isNotBlank()) null else currentState.nameError,
            )
        }
    }

    private fun handleCategorySelected(action: AddAssetAction.CategorySelected) {
        _state.update { it.copy(selectedCategory = action.category) }
    }

    private fun handleColorSelected(colorHex: String) {
        _state.update { it.copy(selectedColorHex = colorHex) }
    }

    private fun handleInitialValueChanged(rawText: String) {
        val digitsOnly = rawText.filter { it.isDigit() }

        val error = when {
            digitsOnly.isEmpty() -> null
            digitsOnly.toLongOrNull() == null -> AddAssetFormError.INVALID_INITIAL_VALUE
            (digitsOnly.toLongOrNull() ?: 0L) < 0L -> AddAssetFormError.NEGATIVE_INITIAL_VALUE
            else -> null
        }

        val formattedDisplay = if (digitsOnly.isNotEmpty()) {
            val amount = digitsOnly.toLongOrNull()
            if (amount != null) {
                formatThousands(amount)
            } else {
                digitsOnly
            }
        } else {
            ""
        }

        _state.update { currentState ->
            currentState.copy(
                initialValueText = formattedDisplay,
                initialValueError = error,
            )
        }
    }

    private fun formatThousands(value: Long): String {
        val raw = value.toString()
        val result = StringBuilder()
        for ((count, i) in (raw.length - 1 downTo 0).withIndex()) {
            if (count > 0 && count % 3 == 0) {
                result.append(' ')
            }
            result.append(raw[i])
        }
        return result.reverse().toString()
    }

    private fun saveAsset() {
        val currentState = _state.value
        val nameTrimmed = currentState.name.trim()

        if (nameTrimmed.isBlank()) {
            _state.update { it.copy(nameError = AddAssetFormError.BLANK_NAME) }
            return
        }

        val category = currentState.selectedCategory ?: return

        val rawAmountString = currentState.initialValueText.replace(" ", "")
        val parsedAmount = if (rawAmountString.isEmpty()) {
            null
        } else {
            rawAmountString.toLongOrNull()
        }

        if (rawAmountString.isNotEmpty() && parsedAmount == null) {
            _state.update { it.copy(initialValueError = AddAssetFormError.INVALID_INITIAL_VALUE) }
            return
        }

        if (parsedAmount != null && parsedAmount < 0L) {
            _state.update { it.copy(initialValueError = AddAssetFormError.NEGATIVE_INITIAL_VALUE) }
            return
        }

        val draft = AssetDraft(
            name = nameTrimmed,
            categoryId = category.id,
            colorHex = currentState.selectedColorHex,
            initialValue = parsedAmount?.let { Money(it) },
            initialValueAsContribution = currentState.initialValueAsContribution,
        )

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            when (val result = addAssetUseCase(draft)) {
                is Outcome.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _effects.send(AddAssetEffect.AssetCreated(draft.name))
                    _effects.send(AddAssetEffect.NavigateBack)
                }
                is Outcome.Failure -> {
                    _state.update { it.copy(isSaving = false) }
                    when (result.error) {
                        AddAssetError.BlankName -> {
                            _state.update { it.copy(nameError = AddAssetFormError.BLANK_NAME) }
                        }
                        AddAssetError.NegativeInitialValue -> {
                            _state.update { it.copy(initialValueError = AddAssetFormError.NEGATIVE_INITIAL_VALUE) }
                        }
                    }
                }
            }
        }
    }
}
