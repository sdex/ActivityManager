package com.sdex.activityrunner.tv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Text

@Composable
fun AccountsSectionDialogButton(
    modifier: Modifier = Modifier,
    text: String,
    shouldRequestFocus: Boolean,
    onClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (shouldRequestFocus) {
            focusRequester.requestFocus()
        }
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .focusable(enabled = false)
            .focusRequester(focusRequester),
        shape = ButtonDefaults.shape(shape = ShapeDefaults.ExtraSmall),
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.surface,
            focusedContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        scale = ButtonDefaults.scale(focusedScale = 1f),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.border),
                shape = ShapeDefaults.ExtraSmall,
            ),
        ),
    ) {
        Text(
            modifier = Modifier
                .widthIn(min = 115.dp)
                .wrapContentWidth(),
            text = text,
        )
    }
}
