package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.ISqlParser;
import com.lhldyf.gallery.java.antlr.tw.SqlParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL Parser的核心逻辑实现
 * @author lhldyf
 * @date 2019-12-01 18:28
 */
@Slf4j
public abstract class AbstractHiveSqlParser<T extends HiveSqlParseRuntimeEntity, R extends SqlParseResult>
        implements ISqlParser {

    /**
     * 声明static 的ParseDriver对象，不确定多线程是否有问题
     */
    private static ParseDriver parseDriver = new ParseDriver();
    private static HiveConf hiveConf = new HiveConf();
    protected static final String TAB = "\t";
    protected static final String DEFAULT_DB = "current_db";

    @Override
    public R parse(String sql, String sqlType) {

        try {

            // 调用hive的解析接口解析成语法树
            String command = removeComment(sql);
            ASTNode astNode = parseDriver.parse(command, null, hiveConf);
            System.out.println(astNode.dumpAsTree());

            // 初始化运行时对象
            T runtimeEntity = constructRuntimeEntity(sqlType);

            // 深度优先遍历语法树
            parseTraversal(astNode, runtimeEntity);

            return constructParseResult(runtimeEntity);

        } catch (Exception e) {
            log.error("SQL解析异常，SQL: {}", sql, e);
            return null;
        }
    }

    /**
     * 构造返回结果
     * @param runtimeEntity
     * @return
     */
    protected abstract R constructParseResult(T runtimeEntity);

    /**
     * 深度优先遍历语法树
     * @param ast
     * @param runtimeEntity
     */
    private Set<String> parseTraversal(ASTNode ast, T runtimeEntity) {
        Set<String> set = new HashSet<>();
        prepareToParseCurrentNodeAndChildren(ast, runtimeEntity);
        set.addAll(parseChildNodes(ast, runtimeEntity));
        set.addAll(parseCurrentNode(ast, runtimeEntity, set));
        endParseCurrentNode(ast, runtimeEntity);
        return set;
    }

    /**
     * 结束解析
     * @param ast
     * @param runtimeEntity
     */
    protected abstract void endParseCurrentNode(ASTNode ast, T runtimeEntity);

    /**
     * 解析当前节点
     * @param ast
     * @param runtimeEntity
     * @param set
     * @return
     */
    protected abstract Set<String> parseCurrentNode(ASTNode ast, T runtimeEntity, Set<String> set);

    /**
     * 解析操作符
     * @param ast
     * @param runtimeEntity
     */
    protected abstract void prepareToParseCurrentNodeAndChildren(ASTNode ast, T runtimeEntity);

    private Set<String> parseChildNodes(ASTNode ast, T runtimeEntity) {
        Set<String> set = new HashSet<>();
        int numCh = ast.getChildCount();
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                set.addAll(parseTraversal(child, runtimeEntity));
            }
        }
        return set;
    }


    /**
     * 构造运行时对象
     * @param sqlType
     * @return
     */
    protected abstract T constructRuntimeEntity(String sqlType);


    // static Pattern removeCommentPattern = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
    static Pattern removeCommentPattern = Pattern.compile("(-+[a-zA-Z0-9_\\u4e00-\\u9fa5]+\\s)");
    static List<Pattern> patterns = new ArrayList<>();
    static {
        patterns.add(removeCommentPattern);
    }

    static String removeComment(String sql) {
        System.out.println("SQL原文: " + sql);

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(sql);
            String fixText;
            while (matcher.find()) {
                fixText = matcher.group();
                sql = sql.replace(fixText, " ");
            }
        }

        System.out.println("去除注释后: " + sql);
        return sql;
    }

}
