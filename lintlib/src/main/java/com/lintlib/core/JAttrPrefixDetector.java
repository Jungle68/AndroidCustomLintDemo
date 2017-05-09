package com.lintlib.core;

import com.android.annotations.NonNull;
import com.android.resources.ResourceFolderType;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.ResourceXmlDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static com.android.SdkConstants.ATTR_NAME;
import static com.android.SdkConstants.TAG_ATTR;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/8
 * @Contact master.jungle68@gmail.com
 */

public class JAttrPrefixDetector extends ResourceXmlDetector {
    public static final String DEFAULT_ERROR_ID = "AttrNotPrefixed";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "You must prefix your custom attr by `ts`";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "We prefix all our attrs to avoid clashes.";
    public static final Issue ISSUE = Issue.create(DEFAULT_ERROR_ID,
            DEFAULT_ERROR_SIMPLE_TIP,
            DEFAULT_ERROR_DETAIL_TIP,
            Category.TYPOGRAPHY,
            5,
            Severity.ERROR,
            new Implementation(JAttrPrefixDetector.class,
                    Scope.RESOURCE_FILE_SCOPE));

    // Only XML files 方法会只保留 XML 文件
    @Override
    public boolean appliesTo(@NonNull Context context,
                             @NonNull File file) {
        return LintUtils.isXmlFile(file);
    }

    // Only values folder  方法会只保留资源文件夹中的values
    @Override
    public boolean appliesTo(ResourceFolderType folderType) {
        return ResourceFolderType.VALUES == folderType;
    }

    // Only attr tag 方法会只保留attr XML 元素
    @Override
    public Collection<String> getApplicableElements() {
        return Collections.singletonList(TAG_ATTR);
    }

    // Only name attribute  方法会只保留name XML 属性
    @Override
    public Collection<String> getApplicableAttributes() {
        return Collections.singletonList(ATTR_NAME);
    }

    @Override
    public void visitElement(XmlContext context, Element element) {
        final Attr attributeNode = element.getAttributeNode(ATTR_NAME);
        if (attributeNode != null) {
            final String val = attributeNode.getValue();
            if (!val.startsWith("android:") && !val.startsWith("ts")) {
                context.report(ISSUE,
                        attributeNode,
                        context.getLocation(attributeNode),
                        DEFAULT_ERROR_SIMPLE_TIP);
            }
        }
    }
}
