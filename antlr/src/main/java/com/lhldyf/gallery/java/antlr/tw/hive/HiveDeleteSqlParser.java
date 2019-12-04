package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * Hive删操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class HiveDeleteSqlParser extends AbstractHiveCrudSqlParser {
    @Override
    protected void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_WHERE == ast.getType()) {
            runtimeEntity.setAffectClause(true);
        }
    }

    @Override
    protected void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_TAB == ast.getType()) {
            // Insert/Update/DELETE 的表
            // 参考格式： (TOK_TAB (TOK_TABNAME default update_table) )
            String db = parseTableDb(ast);
            String tableName = parseTableName(ast);
            runtimeEntity.setCurrDbTable(new String[] {db, tableName});
            putAffectMap(runtimeEntity, db, tableName, SqlParseConstant.COLUMN_ALL);
        }
    }

    @Override
    protected void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (HiveParser.TOK_WHERE == ast.getType()) {
            runtimeEntity.setAffectClause(false);
        }
    }
}
