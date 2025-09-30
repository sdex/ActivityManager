package com.sdex.activityrunner.manifest

import com.sdex.activityrunner.app.ActivityModel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.io.StringReader

class ManifestParser(
    private val manifestXml: String,
) {

    fun getActivities(packageName: String): List<ActivityModel> {
        val activities = mutableListOf<ActivityModel>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(prepareManifest()))
            var eventType = parser.eventType
            var activityModel: ActivityModel? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                    (parser.name == "activity" || parser.name == "activity-alias")
                ) {
                    val className = parser.getAttributeValue(null, "name")
                    val activityName = className.split(".").last()
                    val activityLabel = parser.getAttributeValue(null, "label")
                    val enabledAttributeValue = parser.getAttributeValue(null, "enabled")
                    val enabled = if (enabledAttributeValue.isNullOrEmpty()) {
                        true
                    } else {
                        java.lang.Boolean.parseBoolean(enabledAttributeValue)
                    }
                    val model = ActivityModel(
                        activityName,
                        packageName,
                        className,
                        activityLabel,
                        exported = false,
                        enabled,
                    )

                    val exportedAttributeValue = parser.getAttributeValue(null, "exported")
                    if (exportedAttributeValue.isNullOrEmpty()) {
                        // if the activity has "intent-filter" the exported is true, otherwise false
                        activityModel = model
                    } else {
                        model.exported = java.lang.Boolean.parseBoolean(exportedAttributeValue)
                        activities.add(model)
                    }
                } else if (eventType == XmlPullParser.START_TAG &&
                    parser.name == "intent-filter" && activityModel != null
                ) {
                    activityModel.exported = true
                    activities.add(activityModel)
                    activityModel = null
                } else if (eventType == XmlPullParser.END_TAG &&
                    (parser.name == "activity" || parser.name == "activity-alias") &&
                    activityModel != null
                ) {
                    activityModel.exported = false
                    activities.add(activityModel)
                    activityModel = null
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse manifest for package: $packageName")
        }
        return activities
    }

    private fun prepareManifest(): String {
        return manifestXml.replace(
            "http://schemas.android.com/apk/distribution",
            "android",
        )
    }
}
