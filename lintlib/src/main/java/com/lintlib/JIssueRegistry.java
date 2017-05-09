package com.lintlib;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.lintlib.core.JActivityFragmentLayoutNameDetector;
import com.lintlib.core.JAttrPrefixDetector;
import com.lintlib.core.JBaseActivityDetector;
import com.lintlib.core.JCloseDetector;
import com.lintlib.core.JLogUsageDetector;
import com.lintlib.core.JToastHelperDetector;
import com.lintlib.core.JXmlValuesDetector;

import java.util.Arrays;
import java.util.List;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/8
 * @Contact master.jungle68@gmail.com
 */

public class JIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(JLogUsageDetector.ISSUE,
                JAttrPrefixDetector.ISSUE,
                JCloseDetector.ISSUE,
                JActivityFragmentLayoutNameDetector.ACTIVITY_LAYOUT_NAME_ISSUE,
                JActivityFragmentLayoutNameDetector.FRAGMENT_LAYOUT_NAME_ISSUE,
                JToastHelperDetector.ISSUE,
                JBaseActivityDetector.ISSUE,
                JXmlValuesDetector.ISSUE);
    }
}
