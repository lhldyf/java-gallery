package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive删除表或库操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveTruncateSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
    }

    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_TRUNCATETABLE == ast.getType()) {
            // 参考结构 	(TOK_TRUNCATETABLE (TOK_TABLE_PARTITION (TOK_TABNAME (table1))))
            if (ast.getChild(0).getChild(0).getChildCount() == 1) {
                putAffectMap(runtimeEntity, DEFAULT_DB, ast.getChild(0).getChild(0).getChild(0).getText(),
                             SqlParseConstant.COLUMN_ALL);
            } else if (ast.getChild(0).getChild(0).getChildCount() == 2) {
                putAffectMap(runtimeEntity, ast.getChild(0).getChild(0).getChild(0).getText(),
                             ast.getChild(0).getChild(0).getChild(1).getText(), SqlParseConstant.COLUMN_ALL);
            }
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
    }
}
