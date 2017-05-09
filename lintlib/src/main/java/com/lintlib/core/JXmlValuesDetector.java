package com.lintlib.core;

import com.android.resources.ResourceFolderType;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/8
 * @Contact master.jungle68@gmail.com
 */

public class JXmlValuesDetector extends LayoutDetector {
    public static final String DEFAULT_ERROR_ID = "XmlValues";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "Xml Values HardCoded";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "请在 xml 文件中定义值的大小，不要在布局文件中直接定义";

    public static final Issue ISSUE = Issue.create(DEFAULT_ERROR_ID
            , DEFAULT_ERROR_SIMPLE_TIP
            , DEFAULT_ERROR_DETAIL_TIP
            , Category.I18N
            , 5
            , Severity.ERROR
            , new Implementation(JXmlValuesDetector.class, Scope.RESOURCE_FILE_SCOPE));

    public JXmlValuesDetector() {
    }

    public Speed getSpeed() {
        return Speed.FAST;
    }

    public Collection<String> getApplicableAttributes() {
        List<String> attributes = new ArrayList<>();
        attributes.add("textSize");
        attributes.add("textColor");
        attributes.add("layout_width");
        attributes.add("layout_height");
        attributes.add("paddingLeft");
        attributes.add("paddingRight");
        return attributes;
    }

    public boolean appliesTo(ResourceFolderType folderType) {
        return folderType == ResourceFolderType.LAYOUT
                || folderType == ResourceFolderType.MENU
                || folderType == ResourceFolderType.XML;
    }

    public void visitAttribute(XmlContext context, Attr attribute) {
        String value = attribute.getValue();
        if ((0 != value.length()) && value.charAt(0) != 64 && value.charAt(0) != 63) {
            if (!"http://schemas.android.com/apk/res/android".equals(attribute.getNamespaceURI())) {
                return;
            }

            if (value.startsWith("0")) {
                return;
            }
            if (value.equals("match_parent") || value.equals("wrap_content") || value.equals("fill_parent")) {
                return;
            }

            if (context.getResourceFolderType() == ResourceFolderType.XML) {
                String tagName = attribute.getOwnerDocument().getDocumentElement().getTagName();
                if (!tagName.equals("restrictions")) {
                    return;
                }
            }
            context.report(ISSUE, attribute, context.getLocation(attribute), DEFAULT_ERROR_DETAIL_TIP);
        }

    }

}
