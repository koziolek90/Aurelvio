package com.kris.aurelvio.feature.assets.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kris.aurelvio.feature.assets.presentation.AddAssetEffect
import com.kris.aurelvio.feature.assets.presentation.AddAssetViewModel

@Composable
fun AddAssetRoute(
    viewModel: AddAssetViewModel,
    onNavigateBack: () -> Unit,
    onAssetCreated: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddAssetEffect.NavigateBack -> onNavigateBack()
                is AddAssetEffect.AssetCreated -> {
                    onAssetCreated(effect.name)
                }
                is AddAssetEffect.ShowError -> {
                    // Handled if snackbar host is present
                }
            }
        }
    }

    AddAssetScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
