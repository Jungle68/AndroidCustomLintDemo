package com.lintlib.core;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.lintlib.core.base.InheritBaseDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.ast.Node;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/9
 * @Contact master.jungle68@gmail.com
 */

public class JBaseActivityDetector extends InheritBaseDetector {
    public static final String DEFAULT_ERROR_ID = "BaseActivityNotUsed";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "Base classes for Activity should be used.";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "Please use the base classes for Activity.So that we can do some unified behaviors.";

    public static final Issue ISSUE = Issue.create(DEFAULT_ERROR_ID,
            DEFAULT_ERROR_SIMPLE_TIP,
            DEFAULT_ERROR_DETAIL_TIP,
            Category.CORRECTNESS,
            5,
            Severity.ERROR,
            new Implementation(JBaseActivityDetector.class,
                    Scope.RESOURCE_FILE_SCOPE));

    @Override
    protected HashSet<String> getWrapperClasses() {
        HashSet<String> sets = new HashSet<>();
        sets.add("com.zhiyicx.baseproject.base.TSActivity");
        return sets;
    }

    @Override
    public List<String> applicableSuperClasses() {
        List<String> superclass = new ArrayList<>();
        superclass.add("android.app.Activity");
        superclass.add("android.support.v7.app.AppCompatActivity");
        superclass.add("android.support.v4.app.FragmentActivity");
        return superclass;
    }

    @Override
    protected void reportViolation(JavaContext context, Node node) {
        context.report(ISSUE, node, context.getLocation(node),
                DEFAULT_ERROR_DETAIL_TIP);
    }
}
