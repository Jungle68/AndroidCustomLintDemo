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
import lombok.ast.Block;
import lombok.ast.MethodInvocation;
import lombok.ast.Try;

/**
 * @Describe * 定义代码检查规则
 * 这个是针对 try 和 catch 中的 finally 是否包涵 close 方法进行一个判断
 * 由于要对 java 代码进行扫描,因此继承的是 javascanner 的接口
 * @Author Jungle68
 * @Date 2017/5/9
 * @Contact master.jungle68@gmail.com
 */
public class JCloseDetector extends Detector implements Detector.JavaScanner {
    public static final String DEFAULT_ERROR_ID = "CloseMethod";
    public static final String DEFAULT_ERROR_SIMPLE_TIP = "close 方法应该在 finally 中调用";
    public static final String DEFAULT_ERROR_DETAIL_TIP = "close 方法应该在 finally 中调用,防止导致内存泄漏";

    public static Issue ISSUE = Issue.create(
            DEFAULT_ERROR_ID,
            DEFAULT_ERROR_SIMPLE_TIP,
            DEFAULT_ERROR_DETAIL_TIP,
            // 这个主要是用于对问题的分类，不同的问题就可以集中在一起显示。
            Category.CORRECTNESS,
            // 优先级
            5,
            // 定义查找问题的严重级别
            Severity.ERROR,
            // 提供处理该问题的 Detector 和该 Detector 所关心的资源范围。当系统生成了抽象语法树（Abstract syntax tree，简称AST），或者遍历 xml 资源时，就会调用对应 Issue 的处理器 Detector。
            new Implementation(JCloseDetector.class,
                    Scope.JAVA_FILE_SCOPE)
    );

    // 限定关心的方法的调用类
    public static final String[] sSupportSuperType = new String[]{
            "java.io.InputStream", "java.io.OutputStream", "android.database.Cursor"
    };

    /**
     * 只关心名是close的方法
     *
     * @return
     */
    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("close");
    }

    /**
     * 该方法调用时，会传入代表close方法被调用的节点MethodInvocation,以及所在java文件的上下文JavaContext，
     * 还有AstVisitor。由于我们没有重写createJavaVisitor方法，所以不用管AstVisitor。
     * MethodInvocation封装了close被调用处的代码，而结合JavaContext对象，即可寻找对应的上下文，来帮助我们判断条件。
     *
     * @param context
     * @param visitor
     * @param node
     */
    @Override
    public void visitMethod(JavaContext context, AstVisitor visitor, MethodInvocation node) {
        // 判断类型,看下所监测的资源是否是我们定义的相关资源
        // 通过JavaContext的resolve的方法,传入node节点,由于所有的AST树上的节点都继承自NODE,所以可以通过node去找到class
        JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) context.resolve(node);
        JavaParser.ResolvedClass clzz = method.getContainingClass();
        boolean isSubClass = false;
        for (int i = 0; i < sSupportSuperType.length; i++) {
            if (clzz.isSubclassOf(sSupportSuperType[i], false)) {
                isSubClass = true;
                break;
            }
        }
        if (!isSubClass) super.visitMethod(context, visitor, node);
        /**
         * 查找try和block的信息
         * 在AST中，close 代码节点应该是try的一个子孙节点（try是语法上的block），所以从close代码节点向上追溯，
         * 可以找到对应的try，而Node对象本来就有getParent方法，所以可以递归调用该方法来找到Try节点（这也是一个节点），
         * 或者调用JavaContext的查找限定parent类型的方法:
         */
        Try fTryBlock = context.getParentOfType(node, Try.class);
        int fLineNum = context.getLocation(fTryBlock).getStart().getLine();
        System.out.println("    fLineNum=" + fLineNum);

        /**
         * 如果close在try模块中,接着就要对try进行向上查找,看try是否被包裹在try中,同时,是否处于finally中,try节点
         * 有一个astFinally的方法,可以得到finally的节点,只要判断节点的位置,既可以实现判断close是否在finally中
         */
        Try sTryBlock = context.getParentOfType(fTryBlock, Try.class);
        Block finaBlock = sTryBlock.astFinally();
        int sLineNum = context.getLocation(finaBlock).getStart().getLine();
        System.out.println("    sLineNum=" + sLineNum);
        /**
         * 若我们确定了close是在try 块中，且try块不在finally里，那么就需要触发Issue，这样在html报
         * 告中就可以找到对应的信息了
         * 一个莫名的bug,不能再这里写成
         * if (fLineNum < sLineNum){
         *     context.report(ISSUE, node, context.getLocation(node), "请在finally中调用close");
         * }
         * 否则再直接运行Analyze下Inspect选项后,在inspection窗口中没有办法看到Error from custom lint Check
         * 的错误信息
         */
        if (fLineNum > sLineNum) {
            return;
        } else {
            context.report(ISSUE, node, context.getLocation(node), "please use close method in finally");
        }
    }


}