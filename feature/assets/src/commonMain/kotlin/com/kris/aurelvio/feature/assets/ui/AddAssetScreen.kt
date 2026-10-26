package com.kris.aurelvio.feature.assets.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aurelvio.feature.assets.generated.resources.Res
import aurelvio.feature.assets.generated.resources.add_asset_category_label
import aurelvio.feature.assets.generated.resources.add_asset_color_label
import aurelvio.feature.assets.generated.resources.add_asset_error_blank_name
import aurelvio.feature.assets.generated.resources.add_asset_error_invalid_value
import aurelvio.feature.assets.generated.resources.add_asset_error_negative_value
import aurelvio.feature.assets.generated.resources.add_asset_initial_value_contribution_hint
import aurelvio.feature.assets.generated.resources.add_asset_initial_value_contribution_label
import aurelvio.feature.assets.generated.resources.add_asset_initial_value_currency
import aurelvio.feature.assets.generated.resources.add_asset_initial_value_label
import aurelvio.feature.assets.generated.resources.add_asset_initial_value_placeholder
import aurelvio.feature.assets.generated.resources.add_asset_name_label
import aurelvio.feature.assets.generated.resources.add_asset_name_placeholder
import aurelvio.feature.assets.generated.resources.add_asset_save_button
import aurelvio.feature.assets.generated.resources.add_asset_title
import com.kris.aurelvio.core.designsystem.components.AurelvioCard
import com.kris.aurelvio.core.designsystem.components.BackArrowIcon
import com.kris.aurelvio.core.designsystem.components.CheckIcon
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.core.designsystem.theme.parseHexColor
import com.kris.aurelvio.domain.model.Category
import com.kris.aurelvio.domain.model.CategoryId
import com.kris.aurelvio.feature.assets.presentation.AddAssetAction
import com.kris.aurelvio.feature.assets.presentation.AddAssetFormError
import com.kris.aurelvio.feature.assets.presentation.AddAssetUiState
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    state: AddAssetUiState,
    onAction: (AddAssetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.add_asset_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(AddAssetAction.BackClicked) }) {
                        BackArrowIcon(
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AurelvioSpacing.l, vertical = AurelvioSpacing.m),
            verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.l),
        ) {
            // Asset Name Input
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(AddAssetAction.NameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.add_asset_name_label)) },
                placeholder = { Text(stringResource(Res.string.add_asset_name_placeholder)) },
                singleLine = true,
                isError = state.nameError != null,
                supportingText = state.nameError?.let { error ->
                    {
                        Text(
                            text = when (error) {
                                AddAssetFormError.BLANK_NAME -> stringResource(Res.string.add_asset_error_blank_name)
                                AddAssetFormError.NEGATIVE_INITIAL_VALUE -> stringResource(Res.string.add_asset_error_negative_value)
                                AddAssetFormError.INVALID_INITIAL_VALUE -> stringResource(Res.string.add_asset_error_invalid_value)
                            },
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            // Category Selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.s),
            ) {
                Text(
                    text = stringResource(Res.string.add_asset_category_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                if (state.isLoadingCategories) {
                    CircularProgressIndicator(modifier = Modifier.padding(AurelvioSpacing.s))
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AurelvioSpacing.s),
                        verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.xs),
                    ) {
                        state.categories.forEach { category ->
                            val isSelected = state.selectedCategory?.id == category.id
                            val categoryColor = parseHexColor(category.colorHex)

                            FilterChip(
                                selected = isSelected,
                                onClick = { onAction(AddAssetAction.CategorySelected(category)) },
                                label = { Text(category.name) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(categoryColor),
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                            )
                        }
                    }
                }
            }

            // Color Swatches
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.s),
            ) {
                Text(
                    text = stringResource(Res.string.add_asset_color_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AurelvioSpacing.m),
                ) {
                    state.availableColors.forEach { colorHex ->
                        val isSelected = state.selectedColorHex.equals(colorHex, ignoreCase = true)
                        val color = parseHexColor(colorHex)

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            width = 3.dp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = CircleShape,
                                        )
                                    } else {
                                        Modifier
                                    },
                                )
                                .clickable { onAction(AddAssetAction.ColorSelected(colorHex)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                CheckIcon(tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Initial Value Field
            OutlinedTextField(
                value = state.initialValueText,
                onValueChange = { onAction(AddAssetAction.InitialValueChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.add_asset_initial_value_label)) },
                placeholder = { Text(stringResource(Res.string.add_asset_initial_value_placeholder)) },
                trailingIcon = {
                    Text(
                        text = stringResource(Res.string.add_asset_initial_value_currency),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = AurelvioSpacing.m),
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.initialValueError != null,
                supportingText = state.initialValueError?.let { error ->
                    {
                        Text(
                            text = when (error) {
                                AddAssetFormError.BLANK_NAME -> stringResource(Res.string.add_asset_error_blank_name)
                                AddAssetFormError.NEGATIVE_INITIAL_VALUE -> stringResource(Res.string.add_asset_error_negative_value)
                                AddAssetFormError.INVALID_INITIAL_VALUE -> stringResource(Res.string.add_asset_error_invalid_value)
                            },
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            // Switch for Initial Value as Contribution
            AurelvioCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AurelvioSpacing.l),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.xxs),
                    ) {
                        Text(
                            text = stringResource(Res.string.add_asset_initial_value_contribution_label),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(Res.string.add_asset_initial_value_contribution_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.width(AurelvioSpacing.m))

                    Switch(
                        checked = state.initialValueAsContribution,
                        onCheckedChange = { onAction(AddAssetAction.InitialValueAsContributionChanged(it)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(AurelvioSpacing.l))

            // Save Button
            Button(
                onClick = { onAction(AddAssetAction.SaveClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.canSave,
                shape = RoundedCornerShape(16.dp),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.add_asset_save_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AddAssetScreenPreviewData() {
    AurelvioTheme {
        AddAssetScreen(
            state = AddAssetUiState(
                name = "Konto Oszczędnościowe",
                categories = listOf(
                    Category(CategoryId("1"), "Konta i lokaty", "#4A90E2"),
                    Category(CategoryId("2"), "Inwestycje", "#7ED321"),
                ),
                selectedCategory = Category(CategoryId("1"), "Konta i lokaty", "#4A90E2"),
                initialValueText = "15 000",
                isLoadingCategories = false,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun AddAssetScreenPreviewError() {
    AurelvioTheme {
        AddAssetScreen(
            state = AddAssetUiState(
                name = "",
                nameError = AddAssetFormError.BLANK_NAME,
                categories = listOf(
                    Category(CategoryId("1"), "Konta i lokaty", "#4A90E2"),
                ),
                selectedCategory = Category(CategoryId("1"), "Konta i lokaty", "#4A90E2"),
                initialValueText = "-100",
                initialValueError = AddAssetFormError.NEGATIVE_INITIAL_VALUE,
                isLoadingCategories = false,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun AddAssetScreenPreviewSaving() {
    AurelvioTheme {
        AddAssetScreen(
            state = AddAssetUiState(
                name = "Obligacje 10-letnie",
                categories = listOf(
                    Category(CategoryId("3"), "Obligacje", "#F5A623"),
                ),
                selectedCategory = Category(CategoryId("3"), "Obligacje", "#F5A623"),
                initialValueText = "50 000",
                isSaving = true,
                isLoadingCategories = false,
            ),
            onAction = {},
        )
    }
}
