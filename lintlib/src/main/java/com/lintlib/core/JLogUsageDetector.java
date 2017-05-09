package com.lintlib.core;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;

public class JLogUsageDetector extends Detector implements Detector.JavaScanner {
    public static final String DEFAULT_ERROR_ID = "LogUtilsNotUsed";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "You must avoid to use Log/System.out.println'";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "Logging should be avoided in production for security and performance reasons. Therefore, we created a LogUtils that wraps all our calls to Logger and disable them for release flavor.";

    public static final Issue ISSUE = Issue.create(DEFAULT_ERROR_ID // 我们这条 lint 规则的 id，唯一标识
            , DEFAULT_ERROR_SIMPLE_TIP  // 这条 lint 规则的简短描述
            , DEFAULT_ERROR_DETAIL_TIP // 这条 lint 规则详细的解释
            , Category.MESSAGES // 类别
            , 5                 // 优先级，必须在 1-10之间
            , Severity.ERROR    // 严重程度。其他可用的严重程度还有 FATAL、WARNING、INFORMATIONAL、IGNORE
            , new Implementation(JLogUsageDetector.class, Scope.JAVA_FILE_SCOPE)); // 这是连接 Detector 与 Scope 的桥梁，其中 Detector 的功能是寻找 issue，而 scope 定义了在什么范围内查找 issue。在当前 demo 中，我们需要在字节码级别分析用户有没有使用 android.util.Log

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        // 决定了什么样的类型能够被检测到
        return Collections.<Class<? extends Node>>singletonList(MethodInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitMethodInvocation(MethodInvocation node) {

                if (node.toString().startsWith("System.out.println")) {
                    context.report(ISSUE, node, context.getLocation(node),
                            DEFAULT_ERROR_SIMPLE_TIP);
                    return true;
                }

                JavaParser.ResolvedNode resolve = context.resolve(node);
                if (resolve instanceof JavaParser.ResolvedMethod) {
                    JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolve;
                    // 方法所在的类校验
                    JavaParser.ResolvedClass containingClass = method.getContainingClass();
                    if (containingClass.matches("android.util.Log")) {
                        context.report(ISSUE, node, context.getLocation(node),
                                DEFAULT_ERROR_SIMPLE_TIP);
                        return true;
                    }
                }
                return super.visitMethodInvocation(node);
            }
        };
    }
}
