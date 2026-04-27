package com.yape.home_presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.home_presentation.R

@Composable
fun HomeAddFab(
    expanded: Boolean,
    onFabClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniFabWithLabel(
                    label = stringResource(R.string.fab_camera),
                    icon = Icons.Default.CameraAlt,
                    onClick = onCameraClick
                )
                MiniFabWithLabel(
                    label = stringResource(R.string.fab_gallery),
                    icon = Icons.Default.Image,
                    onClick = onGalleryClick
                )
            }
        }

        FloatingActionButton(onClick = onFabClick) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) stringResource(R.string.fab_close_description)
                else stringResource(R.string.fab_add_description)
            )
        }
    }
}

@Composable
private fun MiniFabWithLabel(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        SmallFloatingActionButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeAddFabPreview(
    @PreviewParameter(ExpandedPreviewProvider::class) expanded: Boolean
) {
    DocVaultTheme {
        HomeAddFab(
            expanded = expanded,
            onFabClick = {},
            onGalleryClick = {},
            onCameraClick = {}
        )
    }
}

private class ExpandedPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}
