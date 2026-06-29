package com.sdex.activityrunner.commons

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.sdex.activityrunner.R
import kotlin.math.max

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false
        applySystemBarInsets()
    }

    private fun applySystemBarInsets() {
        val content = findViewById<View>(android.R.id.content)
        val initialPaddingLeft = content.paddingLeft
        val initialPaddingTop = content.paddingTop
        val initialPaddingRight = content.paddingRight
        val initialPaddingBottom = content.paddingBottom
        var toolbarInitialPaddingTop: Int? = null

        ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val toolbar = findToolbar(view)

            toolbar?.let {
                val initialTop = toolbarInitialPaddingTop ?: toolbar.paddingTop.also { padding ->
                    toolbarInitialPaddingTop = padding
                }
                toolbar.updatePadding(top = initialTop + systemBars.top)
            }

            view.updatePadding(
                left = initialPaddingLeft + systemBars.left,
                top = initialPaddingTop + if (toolbar == null) systemBars.top else 0,
                right = initialPaddingRight + systemBars.right,
                bottom = initialPaddingBottom + max(systemBars.bottom, ime.bottom),
            )
            insets
        }
    }

    private fun findToolbar(root: View): Toolbar? {
        val content = root.findViewById<View>(android.R.id.content)
        return content?.findVisibleViewById(R.id.toolbar) ?: root.findVisibleViewById(R.id.toolbar)
    }

    private inline fun <reified T : View> View.findVisibleViewById(@IdRes id: Int): T? {
        return findViewById<T>(id)?.takeIf { it.isVisible }
    }

    protected fun setupToolbar(isBackButtonEnabled: Boolean = false) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    var subTitle: CharSequence?
        get() {
            return supportActionBar?.subtitle
        }
        set(value) {
            supportActionBar?.subtitle = value
        }
}
