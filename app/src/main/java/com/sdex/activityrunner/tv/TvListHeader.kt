package com.sdex.activityrunner.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.sdex.activityrunner.R

@Composable
internal fun TvListHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    leadingContent: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderContainerHeight)
            .padding(bottom = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        leadingContent?.invoke()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        actions()
    }
}

@Composable
internal fun TvHeaderIconButton(
    modifier: Modifier = Modifier,
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(HeaderButtonSize),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(HeaderIconSize),
        )
    }
}

@Composable
internal fun TvSearchField(
    modifier: Modifier = Modifier,
    query: String,
    hint: String,
    onQueryChange: (String?) -> Unit,
    onMoveFocusToList: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionDown) {
                    keyboardController?.hide()
                    onMoveFocusToList()
                    true
                } else {
                    false
                }
            }
            .border(width = 2.dp, color = borderColor, shape = shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = stringResource(R.string.action_search),
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { onQueryChange(it.takeIf(String::isNotEmpty)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                modifier = Modifier.size(SearchButtonSize),
                onClick = {
                    onQueryChange(null)
                    focusRequester.requestFocus()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Clear search",
                )
            }
        } else {
            Spacer(modifier = Modifier.size(SearchButtonSize))
        }
    }
}

internal val HeaderContainerHeight = 100.dp

private val HeaderButtonSize = 64.dp
private val HeaderIconSize = 32.dp
private val SearchButtonSize = 56.dp

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun TvListHeaderPreview() {
    TvListHeader(
        title = "Activity Manager",
        subtitle = "99 apps",
    ) {
        TvHeaderIconButton(
            icon = R.drawable.ic_search,
            contentDescription = stringResource(R.string.action_search),
            onClick = {},
        )
        TvHeaderIconButton(
            icon = R.drawable.ic_tune,
            contentDescription = stringResource(R.string.action_settings),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun TvSearchFieldPreview() {
    TvSearchField(
        modifier = Modifier.fillMaxWidth(),
        query = "Activity",
        hint = stringResource(R.string.action_search_activity_hint),
        onQueryChange = {},
        onMoveFocusToList = {},
    )
}
