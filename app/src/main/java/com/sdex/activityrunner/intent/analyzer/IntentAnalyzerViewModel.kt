package com.sdex.activityrunner.intent.analyzer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.di.IoDispatcher
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.converter.IntentToLaunchParamsConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IntentAnalyzerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val intentAnalyzer: IntentAnalyzer,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _items = MutableStateFlow<List<AnalyzerItem>>(emptyList())
    val items: StateFlow<List<AnalyzerItem>> = _items

    /**
     * The intercepted intent as it will be handed to the launcher. The system stamps the
     * interceptor alias onto the intent while resolving it, and forwarding that would pre-fill a
     * target pointing straight back at this screen, so it is stripped once here.
     */
    private var forwardedIntent: Intent? = null

    /**
     * Reads the intent off the main thread - resolving the mime type and the handler labels reaches
     * into other applications through binder. Runs once, the result survives a configuration change.
     */
    fun analyze(intent: Intent, source: IntentSource) {
        if (forwardedIntent != null) return
        forwardedIntent = intent.withoutPackage(context.packageName)

        viewModelScope.launch {
            _items.value = withContext(ioDispatcher) { intentAnalyzer.analyze(intent, source) }
        }
    }

    /** Content and file uris the launcher cannot pass on; empty when the hand-off is lossless. */
    fun getUnsupportedUris(): List<Uri> =
        forwardedIntent?.let { IntentToLaunchParamsConverter.getUnsupportedUris(it) }.orEmpty()

    /** The intercepted intent in the editable form the launcher takes, `null` before [analyze]. */
    fun getLaunchParams(): LaunchParams? =
        forwardedIntent?.let { IntentToLaunchParamsConverter(it).convert() }
}

/** A copy without the component and package of [packageName], or the intent itself if it has none. */
private fun Intent.withoutPackage(packageName: String): Intent {
    if (component?.packageName != packageName && `package` != packageName) return this
    return Intent(this).apply {
        if (component?.packageName == packageName) {
            component = null
        }
        if (`package` == packageName) {
            `package` = null
        }
    }
}
