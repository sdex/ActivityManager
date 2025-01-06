package com.sdex.activityrunner.tv.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FilterChipGroup(
    modifier: Modifier = Modifier,
    items: List<String>,
    defaultSelectedItemIndex: Int = 0,
    selectedItemIcon: ImageVector = Icons.Filled.Done,
//    itemIcon: ImageVector = Icons.Filled.Build,
    onSelectionChanged: (Int) -> Unit = {},
) {
    var selectedItemIndex by remember { mutableIntStateOf(defaultSelectedItemIndex) }

    LazyRow(
        modifier = modifier,
        userScrollEnabled = true,
    ) {
        items(items.size) { index: Int ->
            FilterChip(
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = items[selectedItemIndex] == items[index],
                onClick = {
                    selectedItemIndex = index
                    onSelectionChanged(index)
                },
                content = { Text(items[index]) },
                leadingIcon = if (items[selectedItemIndex] == items[index]) {
                    {
                        Icon(
                            imageVector = selectedItemIcon,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    {
//                        Icon(
//                            imageVector = itemIcon,
//                            contentDescription = "Localized description",
//                            modifier = Modifier.size(FilterChipDefaults.IconSize),
//                        )
                    }
                },
            )
        }
    }
}

@Preview
@Composable
fun PreviewFilterChipGroup() {
    FilterChipGroup(
        items = listOf("/POST", "/GET", "/DELETE", "/PUT"),
        onSelectionChanged = {
        },
    )
}
