package pl.kozaps.aurelvio

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.feature.dashboard.presentation.DashboardEffect
import com.kris.aurelvio.feature.dashboard.ui.DashboardRoute
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import aurelvio.feature.dashboard.generated.resources.Res as DashboardRes
import aurelvio.feature.dashboard.generated.resources.snackbar_add_entry
import aurelvio.feature.dashboard.generated.resources.snackbar_asset_detail
import aurelvio.feature.dashboard.generated.resources.snackbar_assets
import aurelvio.feature.dashboard.generated.resources.snackbar_quick_update

@Composable
fun App() {
    AurelvioTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            DashboardRoute(
                onEffect = { effect ->
                    coroutineScope.launch {
                        val message = when (effect) {
                            is DashboardEffect.NavigateToAddEntry ->
                                getString(DashboardRes.string.snackbar_add_entry)
                            is DashboardEffect.NavigateToQuickUpdate ->
                                getString(DashboardRes.string.snackbar_quick_update)
                            is DashboardEffect.NavigateToAssets ->
                                getString(DashboardRes.string.snackbar_assets)
                            is DashboardEffect.NavigateToAsset ->
                                getString(DashboardRes.string.snackbar_asset_detail)
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }
    }
}
