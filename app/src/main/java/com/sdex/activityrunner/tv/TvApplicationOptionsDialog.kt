package com.sdex.activityrunner.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ApplicationOptionsViewModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.tv.common.ActivityManagerTheme
import com.sdex.activityrunner.tv.common.StandardDialog
import com.sdex.activityrunner.util.AppUtils
import com.sdex.activityrunner.util.IntentUtils

@Composable
fun TvApplicationOptionsDialog(
    modifier: Modifier = Modifier,
    item: ApplicationModel,
    viewModel: ApplicationOptionsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    var consumeOpeningSelectKey by remember(item.packageName) { mutableStateOf(true) }
    val canLaunchApp = remember(item.packageName) {
        context.packageManager.getLaunchIntentForPackage(item.packageName) != null
    }

    TvApplicationOptionsDialogContent(
        modifier = modifier.onPreviewKeyEvent { event ->
            if (consumeOpeningSelectKey && event.key.isConfirmKey()) {
                if (event.type == KeyEventType.KeyUp) {
                    consumeOpeningSelectKey = false
                }
                true
            } else {
                false
            }
        },
        item = item,
        canLaunchApp = canLaunchApp,
        onLaunchClick = {
            onDismissRequest()
            IntentUtils.launchApplication(context, item.packageName)
        },
        onPinClick = {
            viewModel.togglePinned(item)
            onDismissRequest()
        },
        onAppInfoClick = {
            onDismissRequest()
            IntentUtils.openApplicationInfo(context, item.packageName)
        },
        onAppMarketClick = {
            onDismissRequest()
            AppUtils.openAppMarket(context, item.packageName)
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun TvApplicationOptionsDialogContent(
    modifier: Modifier = Modifier,
    item: ApplicationModel,
    canLaunchApp: Boolean,
    onLaunchClick: () -> Unit,
    onPinClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppMarketClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    StandardDialog(
        showDialog = true,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AsyncImage(
                    model = rememberApplicationIconModel(item.packageName),
                    contentDescription = item.name,
                    modifier = Modifier.size(dimensionResource(R.dimen.app_icon_size_tv)),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = item.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.packageName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(
                        R.string.app_version_format,
                        item.versionName,
                        item.versionCode,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                val totalActivitiesFormattedText = context.resources.getQuantityString(
                    R.plurals.activities_count,
                    item.activitiesCount,
                    item.activitiesCount,
                )
                Text(
                    text = stringResource(
                        R.string.app_info_activities_number,
                        totalActivitiesFormattedText,
                        item.exportedActivitiesCount,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (canLaunchApp) {
                    ApplicationOptionItem(
                        text = stringResource(R.string.application_option_open),
                        icon = R.drawable.ic_application_launch,
                        onClick = onLaunchClick,
                    )
                }

                ApplicationOptionItem(
                    text = stringResource(
                        if (item.pinnedAt == 0L) {
                            R.string.application_option_pin
                        } else {
                            R.string.application_option_unpin
                        },
                    ),
                    icon = if (item.pinnedAt == 0L) {
                        R.drawable.ic_push_pin_outlined_24
                    } else {
                        R.drawable.ic_push_pin_filled_24
                    },
                    onClick = onPinClick,
                )

                ApplicationOptionItem(
                    text = stringResource(R.string.application_option_open_info),
                    icon = R.drawable.ic_application_open_info,
                    onClick = onAppInfoClick,
                )

                ApplicationOptionItem(
                    text = stringResource(R.string.application_option_open_market),
                    icon = R.drawable.ic_app_market,
                    onClick = onAppMarketClick,
                )
            }
        },
    )
}

private fun Key.isConfirmKey(): Boolean {
    return this == Key.DirectionCenter ||
        this == Key.Enter ||
        this == Key.NumPadEnter ||
        this == Key.Spacebar
}

@Composable
private fun ApplicationOptionItem(
    text: String,
    icon: Int,
    onClick: () -> Unit,
) {
    ListItem(
        selected = false,
        onClick = onClick,
        colors = ListItemDefaults.colors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            pressedContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            pressedContentColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedSelectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
            pressedSelectedContainerColor = MaterialTheme.colorScheme.secondary,
            pressedSelectedContentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        headlineContent = { Text(text = text) },
        leadingContent = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        },
    )
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun TvApplicationOptionsDialogPreview() {
    ActivityManagerTheme {
        TvApplicationOptionsDialogContent(
            modifier = Modifier.width(560.dp),
            item = previewApplicationModel(),
            canLaunchApp = true,
            onLaunchClick = {},
            onPinClick = {},
            onAppInfoClick = {},
            onAppMarketClick = {},
            onDismissRequest = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun TvApplicationOptionsDialogPinnedPreview() {
    ActivityManagerTheme {
        TvApplicationOptionsDialogContent(
            modifier = Modifier.width(560.dp),
            item = previewApplicationModel(pinnedAt = 1_700_000_000_000L),
            canLaunchApp = true,
            onLaunchClick = {},
            onPinClick = {},
            onAppInfoClick = {},
            onAppMarketClick = {},
            onDismissRequest = {},
        )
    }
}

private fun previewApplicationModel(
    pinnedAt: Long = 0L,
) = ApplicationModel(
    packageName = "com.example.sampleapp",
    name = "Sample App",
    activitiesCount = 12,
    exportedActivitiesCount = 4,
    system = false,
    enabled = true,
    versionCode = 100L,
    versionName = "1.0.0",
    updateTime = 1_700_000_000_000L,
    installTime = 1_690_000_000_000L,
    installerPackage = "com.android.vending",
    pinnedAt = pinnedAt,
)
