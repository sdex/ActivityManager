package com.sdex.activityrunner.manifest

import android.content.res.Resources
import android.content.res.XmlResourceParser
import androidx.annotation.WorkerThread
import androidx.core.text.htmlEncode
import com.sdex.activityrunner.util.PackageInfoProvider
import net.dongliu.apk.parser.ApkFile
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.nio.charset.Charset
import javax.inject.Inject
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class ManifestReader @Inject constructor(
    private val packageInfoProvider: PackageInfoProvider,
) {

    @WorkerThread
    fun load(
        packageName: String,
    ): String? {
        try {
            val manifest = parse(packageName)
            return try {
                formatManifest(manifest)
            } catch (_: TransformerException) {
                formatManifest2(manifest)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun parse(packageName: String): String {
        val packageInfo = packageInfoProvider.getPackageInfo(packageName)
        if (packageInfo.splitNames != null) { // it's nullable
            val publicSourceDir = packageInfo.applicationInfo?.publicSourceDir
            val apkFile = ApkFile(publicSourceDir)
            val manifestXml = apkFile.use {
                it.manifestXml
            }
            return manifestXml
        } else {
            val resources = packageInfoProvider.getResourcesForApplication(packageName)
            val parser = resources.assets.openXmlResourceParser("AndroidManifest.xml")
            val stringBuilder = StringBuilder()
            var eventType = parser.next()

            while (eventType != XmlResourceParser.END_DOCUMENT) {
                // start tag found
                if (eventType == XmlResourceParser.START_TAG) {
                    //start with opening element and writing its name
                    stringBuilder.append("<").append(parser.name)

                    // for each attribute in given element append attrName="attrValue"
                    for (i in 0 until parser.attributeCount) {
                        val attributeName = parser.getAttributeName(i)
                        val attributeValue = getAttributeValue(
                            attributeName,
                            parser.getAttributeValue(i),
                            resources,
                        )
                        stringBuilder.append(" ").append(attributeName)
                            .append("=\"").append(attributeValue).append("\"")
                    }

                    stringBuilder.append(">")
                    if (parser.text != null) {
                        // if there is  body of xml element, add it there
                        stringBuilder.append(parser.text)
                    }
                } else if (eventType == XmlResourceParser.END_TAG) {
                    stringBuilder.append("</").append(parser.name).append(">")
                }
                eventType = parser.next()
            }
            return stringBuilder.toString()
        }
    }

    private fun getAttributeValue(
        attributeName: String,
        attributeValue: String,
        resources: Resources,
    ): String {
        if (attributeValue.startsWith("@")) {
            try {
                val id = Integer.valueOf(attributeValue.substring(1))
                val value = if (attributeName == "theme" || attributeName == "resource") {
                    resources.getResourceEntryName(id)
                } else {
                    resources.getString(id)
                }
                return value.htmlEncode()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return attributeValue
    }

    @Throws(TransformerException::class)
    private fun formatManifest(data: String): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val result = StreamResult(StringWriter())
        val dataBytes = data.toByteArray(Charset.forName("UTF-8"))
        val source = StreamSource(ByteArrayInputStream(dataBytes))
        transformer.transform(source, result)
        return result.writer.toString()
    }

    private fun formatManifest2(xml: String?): String {
        if (xml.isNullOrBlank()) return ""
        var stack = 0
        val pretty = StringBuilder()
        val rows = xml.trim()
            .replace(">".toRegex(), ">\n")
            .replace("<".toRegex(), "\n<")
            .split("\n".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (r in rows) {
            if (r.isBlank()) continue
            val row = r.trim()
            if (row.startsWith("<?")) {
                pretty.append(row + "\n")
            } else if (row.startsWith("</")) {
                val indent = repeatString(--stack)
                pretty.append(indent + row + "\n")
            } else if (row.startsWith("<") && !row.endsWith("/>")) {
                val indent = repeatString(stack++)
                pretty.append(indent + row + "\n")
                if (row.endsWith("]]>")) stack--
            } else {
                val indent = repeatString(stack)
                pretty.append(indent + row + "\n")
            }
        }
        return pretty.toString().trim()
    }

    private fun repeatString(stack: Int): String {
        val indent = StringBuilder()
        repeat(stack) {
            indent.append(" ")
        }
        return indent.toString()
    }
}
