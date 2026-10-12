package com.kris.aurelvio.feature.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.dashboard_add_entry
import aurelvio.feature.dashboard.generated.resources.dashboard_update_values
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionButtonsRow(
    onAddEntryClicked: () -> Unit,
    onUpdateValuesClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AurelvioTheme.colors

    Row(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = onAddEntryClicked,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.gold,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(Res.string.dashboard_add_entry),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(AurelvioSpacing.m))

        OutlinedButton(
            onClick = onUpdateValuesClicked,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.gold),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.gold,
            ),
        ) {
            Text(
                text = stringResource(Res.string.dashboard_update_values),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
