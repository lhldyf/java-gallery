package com.lhldyf.gallery.java.antlr.tw.hive;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive改操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveUpdateSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_WHERE == ast.getType()) {
            runtimeEntity.setWhereClause(runtimeEntity.getWhereClauseStack().pop());
            runtimeEntity.setAffectClause(!runtimeEntity.isWhereClause() && runtimeEntity.isAffectClause());
        } else if (HiveParser.TOK_QUERY == ast.getType()) {
            runtimeEntity.setAffectClause(runtimeEntity.getAffectClauseStack().pop());
        }
    }

    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_TAB == ast.getType()) {
            // Insert/Update 的表
            // 参考格式： (TOK_TAB (TOK_TABNAME default update_table) )
            String db = parseTableDb(ast);
            String tableName = parseTableName(ast);
            runtimeEntity.setCurrDbTable(new String[] {db, tableName});
            putAffectMap(runtimeEntity, db, tableName);
        } else if (HiveParser.TOK_UPSET_ELEMENT == ast.getType()) {
            // 更新的列
            // 参考格式： (TOK_UPSET_ELEMENT (column_1) ('1'))
            String db = runtimeEntity.getCurrDbTable()[0];
            String tableName = runtimeEntity.getCurrDbTable()[1];
            putAffectMap(runtimeEntity, db, tableName, ast.getChild(0).getText());
        } else if (HiveParser.TOK_UPSET_ELEMENT_SUBQUERY == ast.getType()) {
            // 更新列带子查询
            // 参考格式： (TOK_UPSET_ELEMENT_SUBQUERY (TOK_TABCOLNAME (column_2)) (TOK_QUERY ...))
            String db = runtimeEntity.getCurrDbTable()[0];
            String tableName = runtimeEntity.getCurrDbTable()[1];
            putAffectMap(runtimeEntity, db, tableName, ast.getChild(0).getChild(0).getText());
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        // 不是WHERE子句时，是TOK_FROM子句，则是affectDb和AffectTable，是TOK_SELECT子句，则是affectColumn
        if (HiveParser.TOK_WHERE == ast.getType()) {
            // 把当前是否where压栈，当这个where结束时，将目前是否where再出栈
            runtimeEntity.getWhereClauseStack().push(runtimeEntity.isWhereClause());
            runtimeEntity.setWhereClause(true);
            runtimeEntity.setAffectClause(false);
        } else if (HiveParser.TOK_QUERY == ast.getType()) {
            runtimeEntity.getAffectClauseStack().push(runtimeEntity.isAffectClause());
            runtimeEntity.setAffectClause(false);
        }
    }
}
