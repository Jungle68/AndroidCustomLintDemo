package com.lintlib.core;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.lintlib.core.base.WrapperBaseDetector;

import java.util.Collections;
import java.util.List;

import lombok.ast.MethodInvocation;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/9
 * @Contact master.jungle68@gmail.com
 */


public class JToastHelperDetector extends WrapperBaseDetector {

    public static final String DEFAULT_ERROR_ID = "MyToastHelper";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "ToastHelper should be used.";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "Please use the wrapper class 'ToastHelper' to show toast.So that we can customize and unify the UI in future.";

    public static final Issue ISSUE = Issue.create(
            DEFAULT_ERROR_ID,
            DEFAULT_ERROR_SIMPLE_TIP,
            DEFAULT_ERROR_DETAIL_TIP,
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(JToastHelperDetector.class, Scope.JAVA_FILE_SCOPE));

    /**
     * Constructs a new {@link JToastHelperDetector} check
     */
    public JToastHelperDetector() {
        super();
    }

    @Override
    protected String getWrapperClassName() {
        return "me.ycdev.android.arch.wrapper.ToastHelper";
    }

    @Override
    protected String[] getTargetClassNames() {
        return new String[]{
                "android.widget.Toast"
        };
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("makeText");
    }

    @Override
    protected void reportViolation(JavaContext context, MethodInvocation node) {
        context.report(ISSUE, node, context.getLocation(node),
                DEFAULT_ERROR_DETAIL_TIP);
    }
}