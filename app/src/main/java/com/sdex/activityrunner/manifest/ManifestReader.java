package com.sdex.activityrunner.manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ManifestReader {

  @Nullable
  @WorkerThread
  public String loadAndroidManifest(Context context, String packageName) {
    try {
      return formatManifest(load(context, packageName));
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String load(Context context, String packageName)
    throws PackageManager.NameNotFoundException, IOException, XmlPullParserException {
    PackageManager packageManager = context.getPackageManager();
    Resources resources = packageManager.getResourcesForApplication(packageName);
    XmlResourceParser parser = resources.getAssets().openXmlResourceParser("AndroidManifest.xml");
    StringBuilder stringBuilder = new StringBuilder();
    int eventType = parser.next();

    while (eventType != XmlResourceParser.END_DOCUMENT) {
      // start tag found
      if (eventType == XmlResourceParser.START_TAG) {
        //start with opening element and writing its name
        stringBuilder.append("<").append(parser.getName());

        //for each attribute in given element append attrName="attrValue"
        for (int i = 0; i < parser.getAttributeCount(); i++) {
          String attributeName = parser.getAttributeName(i);
          String attributeValue = getAttributeValue(attributeName,
            parser.getAttributeValue(i), resources);
          stringBuilder.append(" ").append(attributeName)
            .append("=\"").append(attributeValue).append("\"");
        }

        stringBuilder.append(">");
        if (parser.getText() != null) {
          // if there is  body of xml element, add it there
          stringBuilder.append(parser.getText());
        }
      } else if (eventType == XmlResourceParser.END_TAG) {
        stringBuilder.append("</").append(parser.getName()).append(">");
      }
      eventType = parser.next();
    }
    return stringBuilder.toString();
  }

  private String getAttributeValue(String attributeName, String attributeValue,
                                   Resources resources) {
    if (attributeValue.startsWith("@")) {
      try {
        final Integer id = Integer.valueOf(attributeValue.substring(1));
        final String value;
        if (attributeName.equals("theme") || attributeName.equals("resource")) {
          value = resources.getResourceEntryName(id);
        } else {
          value = resources.getString(id);
        }
        return TextUtils.htmlEncode(value);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return attributeValue;
  }


  private String formatManifest(String data) throws TransformerException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    StreamResult result = new StreamResult(new StringWriter());
    byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
    StreamSource source = new StreamSource(new ByteArrayInputStream(dataBytes));
    transformer.transform(source, result);
    return result.getWriter().toString();
  }
}
