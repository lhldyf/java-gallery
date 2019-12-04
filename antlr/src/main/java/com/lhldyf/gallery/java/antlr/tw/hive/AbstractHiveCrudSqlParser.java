package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseUtils;

import java.util.*;

/**
 * Hive增删改查操作的解析器核心
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public abstract class AbstractHiveCrudSqlParser
        extends AbstractHiveSqlParser<HiveCrudParseRuntimeEntity, HiveCrudSqlParseResult> {

    @Override
    protected HiveCrudSqlParseResult constructParseResult(HiveCrudParseRuntimeEntity runtimeEntity) {
        HiveCrudSqlParseResult result = new HiveCrudSqlParseResult();
        result.setSqlType(runtimeEntity.getSqlType());
        List<String> affectDBList = new ArrayList<>();
        List<String> affectTableList = new ArrayList<>();
        List<String> affectColumnList = new ArrayList<>();
        List<String> relatedDBList = new ArrayList<>();
        List<String> relatedTableList = new ArrayList<>();
        List<String> relatedColumnList = new ArrayList<>();
        tileMap(affectDBList, affectTableList, affectColumnList, runtimeEntity.getAffectMap());
        tileMap(relatedDBList, relatedTableList, relatedColumnList, runtimeEntity.getRelatedMap());
        result.setAffectDBList(affectDBList);
        result.setAffectTableList(affectTableList);
        result.setAffectColumnList(affectColumnList);
        result.setRelatedDBList(relatedDBList);
        result.setRelatedTableList(relatedTableList);
        result.setRelatedColumnList(relatedColumnList);
        result.setLimitCnt(runtimeEntity.getLimitCnt());
        result.setHasFilter(runtimeEntity.isHasFilter());
        return result;
    }

    private void tileMap(List<String> affectDBList, List<String> affectTableList, List<String> affectColumnList,
            Map<String, Map<String, Set<String>>> map) {
        if (!map.isEmpty()) {
            for (Map.Entry<String, Map<String, Set<String>>> dbEntry : map.entrySet()) {
                affectDBList.add(dbEntry.getKey());
                for (Map.Entry<String, Set<String>> tableEntry : dbEntry.getValue().entrySet()) {
                    String tableName = dbEntry.getKey() + SqlParseConstant.DOT + tableEntry.getKey();
                    affectTableList.add(dbEntry.getKey() + SqlParseConstant.DOT + tableEntry.getKey());
                    if (!tableEntry.getValue().isEmpty()) {
                        tableEntry.getValue().forEach(x -> affectColumnList.add(tableName + SqlParseConstant.DOT + x));
                    }
                }
            }
        }
    }

    @Override
    protected void endParseCurrentNode(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (ast.getToken() != null) {
            if (HiveParser.TOK_QUERY == ast.getType()) {
                // 统一逻辑，如果出QUERY，把之前操作的表出栈
                runtimeEntity.setCurrDbTable(runtimeEntity.getCurrTableStack().pop());

            } else if (HiveParser.TOK_WHERE == ast.getType()) {
                runtimeEntity.setWhereClause(runtimeEntity.getWhereClauseStack().pop());
            }
            // 各业务根据实际情况设置是否是affect操作的子句
            endAffectClause(ast, runtimeEntity);
        }
    }

    /**
     * 各业务根据实际情况设置是否是affect操作的子句
     * @param ast
     * @param runtimeEntity
     */
    protected abstract void endAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity);

    @Override
    protected Set<String> parseCurrentNode(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity, Set<String> set) {
        if (ast.getToken() != null) {
            if (HiveParser.TOK_TABREF == ast.getType()) {
                // From 的表
                // 参考格式： (TOK_TABREF (TOK_TABNAME database table_name) ) alias_name
                String db = parseTableDb(ast);
                String tableName = parseTableName(ast);
                if (ast.getChildCount() == 2) {
                    // 如果有两个子节点，第二个子节点为该表的别名
                    String alias = ast.getChild(1).getText();
                    runtimeEntity.getAliasMap().put(alias, new String[] {db, tableName});
                }

                if (ast.getParent().getChildCount() == 1) {
                    runtimeEntity.setCurrDbTable(new String[] {db, tableName});
                } else if (ast.getParent().getChildCount() > 1) {
                    // 父节点有超过1个子节点，说明是多表关联查询，表名都需要通过别名获取，未设置别名的都取为unknown
                    runtimeEntity.setCurrDbTable(SqlParseConstant.UNKNOWN_TABLE);
                }

                if (runtimeEntity.isAffectClause()) {
                    putAffectMap(runtimeEntity, db, tableName);
                } else {
                    putRelatedMap(runtimeEntity, db, tableName);
                }

            } else if (HiveParser.TOK_TABLE_OR_COL == ast.getType()) {
                String column;
                String db;
                String tableName;
                String[] dbTable;
                if (HiveParser.DOT == ast.getParent().getType()) {
                    // 如果父节点是DOT，说明是有别名的，把别名对应的`库名.表名`解析出来
                    // 当前节点的子节点为别名
                    String alias = ast.getChild(0).getText();
                    // DOT的第二个节点为具体的列名
                    column = ast.getParent().getChild(1).getText();
                    // 获取别名对应的库.表
                    dbTable = runtimeEntity.getAliasMap().get(alias);
                } else {
                    // 父节点不是DOT，取上下文的库表
                    dbTable = runtimeEntity.getCurrDbTable();
                    column = ast.getChild(0).getText();
                }

                db = dbTable[0];
                tableName = dbTable[1];

                runtimeEntity.setHasFilter(true);
                // 如果当前是where子句，那么加入到related，若不是where子句，加入到affect
                if (runtimeEntity.isAffectClause()) {
                    putAffectMap(runtimeEntity, db, tableName, column);
                } else {
                    putRelatedMap(runtimeEntity, db, tableName, column);
                }

            } else if (HiveParser.TOK_ALLCOLREF == ast.getType()) {
                // SQL中的`*`

                String column = "*";
                String db;
                String tableName;
                String[] dbTable;
                if (ast.getChildCount() == 1) {
                    // 如果存在子节点，那么孙子节点是别名
                    String alias = ast.getChild(0).getChild(0).getText();
                    dbTable = runtimeEntity.getAliasMap().get(alias);
                } else {
                    dbTable = runtimeEntity.getCurrDbTable();
                }

                db = dbTable[0];
                tableName = dbTable[1];
                // 如果当前是where子句，那么加入到related，若不是where子句，加入到affect
                if (runtimeEntity.isAffectClause()) {
                    putAffectMap(runtimeEntity, db, tableName, column);
                } else {
                    putRelatedMap(runtimeEntity, db, tableName, column);
                }
            } else {
                customParseCurrent(ast, runtimeEntity);
            }
        }
        return set;
    }

    /**
     * 留给各个节点实现自定义解析
     * @param ast
     * @param runtimeEntity
     */
    protected abstract void customParseCurrent(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity);

    @Override
    protected void prepareToParseCurrentNodeAndChildren(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity) {
        if (ast.getToken() != null) {
            if (HiveParser.TOK_QUERY == ast.getType()) {
                // 统一逻辑，如果进入QUERY，把当前正在曹邹的表压栈
                runtimeEntity.getCurrTableStack().push(runtimeEntity.getCurrDbTable());
                runtimeEntity.setCurrDbTable(SqlParseConstant.UNKNOWN_TABLE);

            } else if (HiveParser.TOK_WHERE == ast.getType()) {
                // 把当前是否where压栈，当这个where结束时，将目前是否where再出栈
                runtimeEntity.getWhereClauseStack().push(runtimeEntity.isWhereClause());
                runtimeEntity.setWhereClause(true);
            }

            prepareAffectClause(ast, runtimeEntity);
        }
    }

    /**
     * 各业务根据实际情况设置是否是affect操作的子句
     * @param ast
     * @param runtimeEntity
     */
    protected abstract void prepareAffectClause(ASTNode ast, HiveCrudParseRuntimeEntity runtimeEntity);

    @Override
    protected HiveCrudParseRuntimeEntity constructRuntimeEntity(String sqlType) {
        HiveCrudParseRuntimeEntity entity = new HiveCrudParseRuntimeEntity();
        entity.setSqlType(sqlType);
        return entity;
    }

    protected String parseDbAndTable(ASTNode tabTree, HiveCrudParseRuntimeEntity runtimeEntity) {
        String tableName = ParseUtils.getUnescapedName((ASTNode) tabTree.getChild(0));
        if (tabTree.getChildCount() == 1) {
            runtimeEntity.getDatabases().add(DEFAULT_DB);
            tableName = DEFAULT_DB + "." + tableName;
        } else {
            runtimeEntity.getDatabases().add(tableName);
            tableName = tableName + "." + tabTree.getChild(1);
        }
        return tableName;
    }

    public static String unEscapeIdentifier(String val) {
        if (val == null) {
            return null;
        }
        if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    /**
     * 解析出 TOK_TAB/TOK_TABREF 的库名
     * @param ast TOK_TAB/TOK_TABREF对象
     * @return
     */
    protected String parseTableDb(ASTNode ast) {
        ASTNode tableNameNode = (ASTNode) ast.getChild(0);
        if (tableNameNode.getChildCount() == 2) {
            return tableNameNode.getChild(0).getText();
        } else {
            return DEFAULT_DB;
        }
    }

    /**
     * 解析出 TOK_TAB/TOK_TABREF 的表名
     * @param ast TOK_TAB/TOK_TABREF对象
     * @return
     */
    protected String parseTableName(ASTNode ast) {
        ASTNode tableNameNode = (ASTNode) ast.getChild(0);
        if (tableNameNode.getChildCount() == 2) {
            return tableNameNode.getChild(1).getText();
        } else {
            return tableNameNode.getChild(0).getText();
        }
    }

    protected String fullTableName(String db, String tableName) {
        if (StringUtils.isEmpty(db)) {
            return DEFAULT_DB + "." + tableName;
        } else {
            return db + "." + tableName;
        }
    }

    protected void putAffectMap(HiveCrudParseRuntimeEntity entity, String database, String tableName) {
        if (StringUtils.isEmpty(database)) {
            database = DEFAULT_DB;
        }

        entity.getAffectMap().computeIfAbsent(database, k -> new HashMap<>(16));
        entity.getAffectMap().get(database).computeIfAbsent(tableName, k -> new HashSet<>());
    }

    protected void putAffectMap(HiveCrudParseRuntimeEntity entity, String database, String tableName, String column) {
        if (StringUtils.isEmpty(database)) {
            database = DEFAULT_DB;
        }

        putAffectMap(entity, database, tableName);
        entity.getAffectMap().get(database).get(tableName).add(column);
    }

    protected void putRelatedMap(HiveCrudParseRuntimeEntity entity, String database, String tableName) {
        if (StringUtils.isEmpty(database)) {
            database = DEFAULT_DB;
        }

        entity.getRelatedMap().computeIfAbsent(database, k -> new HashMap<>(16));
        entity.getRelatedMap().get(database).computeIfAbsent(tableName, k -> new HashSet<>());
    }

    protected void putRelatedMap(HiveCrudParseRuntimeEntity entity, String database, String tableName, String column) {
        if (StringUtils.isEmpty(database)) {
            database = DEFAULT_DB;
        }

        putRelatedMap(entity, database, tableName);
        entity.getRelatedMap().get(database).get(tableName).add(column);
    }
}
