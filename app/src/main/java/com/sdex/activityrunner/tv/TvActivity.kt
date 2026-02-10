package com.sdex.activityrunner.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.Surface
import coil.Coil
import coil.ImageLoader
import com.sdex.activityrunner.R
import com.sdex.activityrunner.tv.common.ActivityManagerTheme
import com.sdex.activityrunner.tv.common.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer

@AndroidEntryPoint
class TvActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val iconSize = resources.getDimensionPixelSize(R.dimen.app_icon_size_tv)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(AppIconKeyer())
                    add(
                        AppIconFetcher.Factory(
                            iconSize,
                            false,
                            this@TvActivity,
                        ),
                    )
                }
                .build(),
        )

        setContent {
            ActivityManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    NavigationGraph()
                }
            }
        }
    }
}
