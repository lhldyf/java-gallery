package com.lhldyf.gallery.java.antlr.tw.hive;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive查操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveSelectSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_LIMIT == ast.getType()) {
            runtimeEntity.setLimitCnt(Integer.valueOf(ast.getChild(ast.getChildCount() - 1).getText()));
        }
    }

    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_WHERE == ast.getType()) {
            runtimeEntity.setWhereClause(runtimeEntity.getWhereClauseStack().pop());
        } else if (HiveParser.TOK_SELECT == ast.getType() || HiveParser.TOK_FROM == ast.getType()) {
            runtimeEntity.setAffectClause(runtimeEntity.getAffectClauseStack().pop());
        } else if (HiveParser.TOK_JOIN == ast.getType()) {
            runtimeEntity.setAffectClause(runtimeEntity.getAffectClauseStack().pop());
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        // 不是WHERE子句时，是TOK_FROM子句，则是affectDb和AffectTable，是TOK_SELECT子句，则是affectColumn
        if (HiveParser.TOK_WHERE == ast.getType()) {
            // 把当前是否where压栈，当这个where结束时，将目前是否where再出栈
            runtimeEntity.getWhereClauseStack().push(runtimeEntity.isWhereClause());
            runtimeEntity.setWhereClause(true);
        } else if (HiveParser.TOK_SELECT == ast.getType() || HiveParser.TOK_FROM == ast.getType()) {
            runtimeEntity.getAffectClauseStack().push(runtimeEntity.isAffectClause());
            runtimeEntity.setAffectClause(!runtimeEntity.isWhereClause());
        } else if (HiveParser.EQUAL == ast.getType() && HiveParser.TOK_JOIN == ast.getParent().getType()) {
            runtimeEntity.getAffectClauseStack().push(runtimeEntity.isAffectClause());
            runtimeEntity.setAffectClause(false);
        }
    }
}
