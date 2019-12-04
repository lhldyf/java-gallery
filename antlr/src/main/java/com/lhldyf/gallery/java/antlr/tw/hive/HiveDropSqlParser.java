package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive删除表或库操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveDropSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
    }

    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_DROPTABLE == ast.getType()) {
            if (ast.getChild(0).getChildCount() == 1) {
                putAffectMap(runtimeEntity, DEFAULT_DB, ast.getChild(0).getChild(0).getText(),
                             SqlParseConstant.COLUMN_ALL);
            } else if (ast.getChild(0).getChildCount() == 2) {
                String db = ast.getChild(0).getChild(0).getText();
                String tableName = ast.getChild(0).getChild(1).getText();
                putAffectMap(runtimeEntity, db, tableName, SqlParseConstant.COLUMN_ALL);
            }
        } else if (HiveParser.TOK_DROPDATABASE == ast.getType()) {
            putAffectMap(runtimeEntity, ast.getChild(0).getText(), SqlParseConstant.COLUMN_ALL,
                         SqlParseConstant.COLUMN_ALL);
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
    }
}
