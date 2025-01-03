package com.sdex.activityrunner.tv

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Text

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class,
)
@Composable
fun ConfigDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            AccountsSectionDialogButton(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(android.R.string.ok),
                shouldRequestFocus = false,
                onClick = onDismissRequest,
            )
        },
        dismissButton = {
            AccountsSectionDialogButton(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(android.R.string.cancel),
                shouldRequestFocus = false,
                onClick = onDismissRequest,
            )
        },
        title = {
        },
        text = {
            Column(
                modifier = modifier.wrapContentHeight(),
            ) {
                Text(text = "Sort by")

                FilterChipGroup(
                    modifier = Modifier.padding(top = 20.dp),
                    items = listOf("Name", "Update time", "Install time"),
                    onSelectionChanged = {

                    },
                )

                FilterChipGroup(
                    modifier = Modifier.padding(top = 20.dp),
                    items = listOf("Ascending", "Descending"),
                    onSelectionChanged = {

                    },
                )

            }
        },

        containerColor = MaterialTheme.colorScheme.surface,
        shape = ShapeDefaults.ExtraSmall,
    )
}
