package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive增操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveInsertSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_INSERT_INTO == ast.getType()) {
            runtimeEntity.setAffectClause(false);
        }
    }

    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_TAB == ast.getType()) {
            // Insert/Update 的表
            // 参考格式： TOK_TABREF (TOK_TABNAME (table1)) (alia1))
            String db = parseTableDb(ast);
            String tableName = parseTableName(ast);
            runtimeEntity.setCurrDbTable(new String[] {db, tableName});
            if (ast.getChildCount() == 1) {
                putAffectMap(runtimeEntity, db, tableName, SqlParseConstant.COLUMN_ALL);
            } else if (ast.getChildCount() == 2) {
                // 第二个子节点指定插入哪些列
                ASTNode tableColName = (ASTNode) ast.getChild(1);
                for (int i = 0; i < tableColName.getChildCount(); i++) {
                    putAffectMap(runtimeEntity, db, tableName, tableColName.getChild(i).getText());
                }
            }
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        // 不是WHERE子句时，是TOK_FROM子句，则是affectDb和AffectTable，是TOK_SELECT子句，则是affectColumn
        if (HiveParser.TOK_INSERT_INTO == ast.getType()) {
            runtimeEntity.setAffectClause(true);
        }
    }
}
