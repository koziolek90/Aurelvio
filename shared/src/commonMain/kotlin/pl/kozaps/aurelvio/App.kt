package pl.kozaps.aurelvio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.domain.repository.fake.FakeAssetRepository
import com.kris.aurelvio.domain.repository.fake.FakeCategoryRepository
import com.kris.aurelvio.domain.repository.fake.FakeEntryRepository
import com.kris.aurelvio.domain.usecase.AddAssetUseCase
import com.kris.aurelvio.domain.usecase.GetCategoriesUseCase
import com.kris.aurelvio.feature.assets.presentation.AddAssetViewModel
import com.kris.aurelvio.feature.assets.ui.AddAssetRoute
import com.kris.aurelvio.feature.dashboard.presentation.DashboardEffect
import com.kris.aurelvio.feature.dashboard.ui.DashboardRoute
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import aurelvio.feature.dashboard.generated.resources.Res as DashboardRes
import aurelvio.feature.dashboard.generated.resources.snackbar_asset_detail
import aurelvio.feature.dashboard.generated.resources.snackbar_assets
import aurelvio.feature.dashboard.generated.resources.snackbar_quick_update

private sealed interface Screen {
    data object Dashboard : Screen
    data object AddAsset : Screen
}

@Composable
fun App() {
    AurelvioTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

        // Shared fake repositories for runtime interaction
        val categoryRepository = remember { FakeCategoryRepository() }
        val assetRepository = remember { FakeAssetRepository() }
        val entryRepository = remember { FakeEntryRepository() }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.Dashboard -> {
                        DashboardRoute(
                            onEffect = { effect ->
                                when (effect) {
                                    DashboardEffect.NavigateToAddEntry -> {
                                        currentScreen = Screen.AddAsset
                                    }
                                    DashboardEffect.NavigateToQuickUpdate -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(getString(DashboardRes.string.snackbar_quick_update))
                                        }
                                    }
                                    DashboardEffect.NavigateToAssets -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(getString(DashboardRes.string.snackbar_assets))
                                        }
                                    }
                                    is DashboardEffect.NavigateToAsset -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(getString(DashboardRes.string.snackbar_asset_detail))
                                        }
                                    }
                                }
                            }
                        )
                    }
                    Screen.AddAsset -> {
                        val addAssetUseCase = remember { AddAssetUseCase(assetRepository, entryRepository) }
                        val getCategoriesUseCase = remember { GetCategoriesUseCase(categoryRepository) }
                        val addAssetViewModel = remember { AddAssetViewModel(getCategoriesUseCase, addAssetUseCase) }

                        AddAssetRoute(
                            viewModel = addAssetViewModel,
                            onNavigateBack = {
                                currentScreen = Screen.Dashboard
                            },
                            onAssetCreated = { assetName ->
                                currentScreen = Screen.Dashboard
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Dodano aktywo: $assetName")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
