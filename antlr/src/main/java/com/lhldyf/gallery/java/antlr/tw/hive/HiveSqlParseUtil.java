package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.parse.*;

import java.util.*;

/**
 * 目的：获取AST中的表，列，以及对其所做的操作，如SELECT,SQL_TYPE_INSERT
 * 重点：获取SELECT操作中的表和列的相关操作。其他操作这判断到表级别。
 * 实现思路：对AST深度优先遍历，遇到操作的token则判断当前的操作，
 * 遇到TOK_TAB或TOK_TABREF则判断出当前操作的表，遇到子句则压栈当前处理，处理子句。
 * 子句处理完，栈弹出。
 * @author lhldyf
 * @date 2019-11-25 18:36
 */
public class HiveSqlParseUtil {

    /**
     * 声明static 的ParseDriver对象，不确定多线程是否有问题
     */
    private static ParseDriver parseDriver = new ParseDriver();
    private static HiveConf hiveConf = new HiveConf();

    private static final String UNKNOWN = "UNKNOWN";

    /** 未选择数据库时默认的库名 **/
    public static final String DEFAULT_DB = "current_db";


    // 为多线程执行时创建ThreadLocal变量
    /** 表的别名 **/
    private static ThreadLocal<Map<String, String>> alias = ThreadLocal.withInitial(HashMap::new);
    /** 影响的列 **/
    private static ThreadLocal<Map<String, String>> cols = ThreadLocal.withInitial(TreeMap::new);
    /** 列的别名 **/
    private static ThreadLocal<TreeMap<String, String>> colAlias = ThreadLocal.withInitial(TreeMap::new);
    /** 操作的表 **/
    private static ThreadLocal<Set<String>> tables = ThreadLocal.withInitial(HashSet::new);
    /** 操作的库 **/
    private static ThreadLocal<Set<String>> databases = ThreadLocal.withInitial(HashSet::new);
    /** 表名的栈 **/
    private static ThreadLocal<Stack<String>> tableNameStack = ThreadLocal.withInitial(Stack::new);
    /** 操作的栈 **/
    private static ThreadLocal<Stack<HiveSqlTypeEnum>> sqlTypeStack = ThreadLocal.withInitial(Stack::new);
    /**
     * 定义及处理不清晰，修改为query或from节点对应的table集合或许好点。目前正在查询处理的表可能不止一个。
     */
    private static ThreadLocal<String> nowQueryTable = ThreadLocal.withInitial(() -> "");
    /** 当前正在识别的操作 **/
    private static ThreadLocal<HiveSqlTypeEnum> nowSqlType = new ThreadLocal<>();
    /** 当前是否是join从句 **/
    private static ThreadLocal<Boolean> joinClause = ThreadLocal.withInitial(() -> false);
    /** 本次操作的操作类型 **/
    private static ThreadLocal<HiveSqlTypeEnum> sqlType = new ThreadLocal<>();



    public enum HiveSqlTypeEnum {
        SELECT,
        INSERT,
        DROP,
        TRUNCATE,
        LOAD,
        CREATETABLE,
        ALTER,
        UPDATE,
        GRANT,
        REVOKE,
        DELETE
    }

    public static void main(String[] args) throws Exception {
        // parse("Select name,ip from zpc2 bieming where age > 10 and area in (select area from system.city)");
        // parse("select * from table1");
        parse("select col_1, col_2 from table1, table2 where col_3=col_4");
        parse("select t1.col_1, t2.col_2 from table1 t1 join table2 t2 on t1.col3 = t2.col4");
        // parse("insert into table db1.desc_table select * from db2.source_table");
        // parse("insert into table desc_table(a,b,c) values(1,2,3)");
        // parse("insert into table desc_table values(1,2,3)");
        // parse("update update_table set column_1 = '1', column_2=2 where column_3 = '3'");
        // parse("DROP TABLE fq1");
        // parse("delete from delete_table where column_1 in (select area from system.city)");
        // parse("grant create on *.* to role public, user user1");
        // parse("SQL_TYPE_GRANT CREATE ON DATABASE default TO user mllib;");
        // parse("SQL_TYPE_GRANT ALL on table authorization_test to group test_role");
        // parse("grant select on * to role public");
        // parse("grant role1  to role public");
        // parse("SQL_TYPE_GRANT r1,r2 TO USER user1, user user2;");
        // parse("SQL_TYPE_GRANT SQL_TYPE_SELECT ON ta TO USER alice, ROLE r1 WITH SQL_TYPE_GRANT OPTION;");
        // parse("revoke all on database spark from user mllib");
        // SqlParseResult parseInfo = parse("SQL_TYPE_GRANT r1 TO USER user1;");
    }

    public static SqlParseResult parse(String sql) throws Exception {
        sqlType.set(parseSqlType(sql));
        if (null == sqlType.get()) {
            System.out.println("暂不支持该SQL的解析");
            System.out.println(sql);
            return null;
        }

        ASTNode astNode = parseDriver.parse(sql, null, hiveConf);
        System.out.println(astNode.dumpAsTree());
        parseIteral(astNode);
        System.out.println("***************表***************");
        for (String table : tables.get()) {
            System.out.println(table);
        }
        System.out.println("***************列***************");
        output(cols.get());
        System.out.println("***************别名***************");
        output(alias.get());

        HiveCrudSqlParseResult parseInfo = new HiveCrudSqlParseResult();
        parseInfo.setSqlType(sqlType.get().name());
        parseInfo.setAffectDBList(new ArrayList<>(databases.get()));
        parseInfo.setAffectColumnList(new ArrayList<>(cols.get().keySet()));
        setTableList(parseInfo);
        alias.remove();
        cols.remove();
        colAlias.remove();
        tables.remove();
        tableNameStack.remove();
        sqlTypeStack.remove();
        nowQueryTable.remove();
        nowSqlType.remove();
        joinClause.remove();
        databases.remove();
        sqlType.remove();

        System.out.println("---- 解析结束 ----");
        System.out.println(parseInfo);
        return parseInfo;
    }

    /**
     * 根据databases设置影响的数据库和关联的数据库
     * @param parseInfo
     */
    private static void setTableList(HiveCrudSqlParseResult parseInfo) {
        List<String> tableList = new ArrayList<>(tables.get());
        String curSqlType = sqlType.get().name();
        for (String table : tableList) {
            String[] tableArr = table.split("\t");
            if (tableArr.length == 2) {
                if (tableArr[1].equals(curSqlType)) {
                    parseInfo.getAffectTableList().add(tableArr[0]);
                } else {
                    parseInfo.getRelatedTableList().add(tableArr[0]);
                }
            } else {
                parseInfo.getRelatedTableList().add(table);
            }

        }
    }


    private static HiveSqlTypeEnum parseSqlType(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        int index = sql.indexOf(" ");
        if (index < 1) {
            System.out.println("异常SQL: " + sql);
            return null;
        }

        String key = sql.substring(0, index).toUpperCase();
        return HiveSqlTypeEnum.valueOf(key);
    }

    private static HiveSqlTypeEnum parseSqlType(ASTNode astNode) {
        switch (astNode.getChild(0).getType()) {
            case HiveParser.TOK_QUERY:
                // 由于INSERT INTO TABLE SQL_TYPE_SELECT 和 SQL_TYPE_SELECT 的第一个节点都是TOK_QUERY,无法使用这种判断方式
                return HiveSqlTypeEnum.SELECT;
            case HiveParser.TOK_INSERT:
            case HiveParser.TOK_INSERT_INTO:
                return HiveSqlTypeEnum.INSERT;
            case HiveParser.TOK_DELETE:
                return HiveSqlTypeEnum.DELETE;
            case HiveParser.TOK_UPDATE:
                return HiveSqlTypeEnum.UPDATE;
            case HiveParser.TOK_GRANT:
                return HiveSqlTypeEnum.GRANT;
            case HiveParser.TOK_REVOKE:
                return HiveSqlTypeEnum.REVOKE;
            case HiveParser.TOK_DROPTABLE:
                return HiveSqlTypeEnum.DROP;
            default:
                return null;
        }
    }

    private static Set<String> parseIteral(ASTNode ast) {
        //当前查询所对应到的表集合
        Set<String> set = new HashSet<>();
        prepareToParseCurrentNodeAndChilds(ast);
        set.addAll(parseChildNodes(ast));
        set.addAll(parseCurrentNode(ast, set));
        endParseCurrentNode(ast);
        return set;
    }


    private static void prepareToParseCurrentNodeAndChilds(ASTNode ast) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                //join 从句开始
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_JOIN:
                    joinClause.set(true);
                    break;
                // 查询语句开始
                case HiveParser.TOK_QUERY:
                    tableNameStack.get().push(nowQueryTable.get());
                    sqlTypeStack.get().push(nowSqlType.get());
                    nowQueryTable.set("");
                    nowSqlType.set(HiveSqlTypeEnum.SELECT);
                    break;
                case HiveParser.TOK_INSERT:
                case HiveParser.TOK_INSERT_INTO:
                    tableNameStack.get().push(nowQueryTable.get());
                    sqlTypeStack.get().push(nowSqlType.get());
                    nowSqlType.set(HiveSqlTypeEnum.INSERT);
                    break;
                case HiveParser.TOK_SELECT:
                    tableNameStack.get().push(nowQueryTable.get());
                    sqlTypeStack.get().push(nowSqlType.get());
                    nowSqlType.set(HiveSqlTypeEnum.SELECT);
                    break;
                case HiveParser.TOK_DROPTABLE:
                    nowSqlType.set(HiveSqlTypeEnum.DROP);
                    break;
                case HiveParser.TOK_TRUNCATETABLE:
                    nowSqlType.set(HiveSqlTypeEnum.TRUNCATE);
                    break;
                case HiveParser.TOK_UPDATE:
                    nowSqlType.set(HiveSqlTypeEnum.UPDATE);
                    break;
                case HiveParser.TOK_LOAD:
                    nowSqlType.set(HiveSqlTypeEnum.LOAD);
                    break;
                case HiveParser.TOK_CREATETABLE:
                    nowSqlType.set(HiveSqlTypeEnum.CREATETABLE);
                    break;
                case HiveParser.TOK_GRANT:
                    nowSqlType.set(HiveSqlTypeEnum.GRANT);
                    break;
                case HiveParser.TOK_REVOKE:
                case HiveParser.TOK_REVOKE_PERMISSION:
                case HiveParser.TOK_REVOKE_ROLE:
                    nowSqlType.set(HiveSqlTypeEnum.REVOKE);
                    break;
                default:
                    break;
            }
            if (ast.getToken() != null && ast.getToken().getType() >= HiveParser.TOK_ALTERDATABASE_PROPERTIES
                    && ast.getToken().getType() <= HiveParser.TOK_ALTERVIEW_RENAME) {
                nowSqlType.set(HiveSqlTypeEnum.ALTER);
            }
        }
    }

    private static Set<String> parseChildNodes(ASTNode ast) {
        Set<String> set = new HashSet<>();
        int numCh = ast.getChildCount();
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                set.addAll(parseIteral(child));
            }
        }
        return set;
    }


    private static Set<String> parseCurrentNode(ASTNode ast, Set<String> set) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_TABLE_PARTITION:
                    // case HiveParser.TOK_TABNAME:
                    if (ast.getChildCount() != 2) {
                        String table = ParseUtils.getUnescapedName((ASTNode) ast.getChild(0));
                        if (nowSqlType.get() == HiveSqlTypeEnum.SELECT) {
                            nowQueryTable.set(table);
                        }
                        tables.get().add(table + "\t" + nowSqlType.get());
                    }
                    break;

                case HiveParser.TOK_TAB:
                    // insert到一张表，这个表名的爷爷就是TOK_TAB
                    // 参考格式： (TOK_TAB (TOK_TABNAME db1 desc_table))
                    String outputTable = parseDbAndTable((ASTNode) ast.getChild(0));
                    // if (nowSqlType.get() == SqlParseConstant.SQL_TYPE_SELECT) {
                    nowQueryTable.set(outputTable);
                    // }


                    tables.get().add(outputTable + "\t" + nowSqlType.get());
                    break;
                case HiveParser.TOK_TABREF:
                    // 参考格式： (TOK_TABREF (TOK_TABNAME database table_name) ) alias_name

                    // tabTree: TOK_TABNAME database table_name
                    String tableName = parseDbAndTable((ASTNode) ast.getChild(0));

                    if (nowSqlType.get() == HiveSqlTypeEnum.SELECT) {
                        if (joinClause.get() && !"".equals(nowQueryTable.get())) {
                            nowQueryTable.set(nowQueryTable.get() + "&" + tableName);
                        } else {
                            nowQueryTable.set(tableName);
                        }
                        set.add(tableName);
                    }
                    tables.get().add(tableName + "\t" + nowSqlType.get());
                    if (ast.getChild(1) != null) {
                        String alia = ast.getChild(1).getText().toLowerCase();
                        //sql6 p别名在tabref只对应为一个表的别名。
                        alias.get().put(alia, tableName);
                    }
                    break;
                case HiveParser.TOK_TABLE_OR_COL:
                    if (ast.getParent().getType() != HiveParser.DOT) {
                        String col = ast.getChild(0).getText().toLowerCase();
                        if (alias.get().get(col) == null
                                && colAlias.get().get(nowQueryTable.get() + "." + col) == null) {
                            if (nowQueryTable.get().indexOf("&") > 0) {
                                //sql23
                                // cols.get().put(UNKNOWN + "." + col, "");
                            } else {
                                cols.get().put(nowQueryTable.get() + "." + col, "");
                            }
                        }
                    }
                    break;
                case HiveParser.TOK_ALLCOLREF:
                    cols.get().put(nowQueryTable.get() + ".*", "");
                    break;
                case HiveParser.TOK_SUBQUERY:
                    if (ast.getChildCount() == 2) {
                        String tableAlias = unescapeIdentifier(ast.getChild(1).getText());
                        String aliaReal = "";
                        for (String table : set) {
                            aliaReal = aliaReal + table + "&";
                        }
                        if (aliaReal.length() != 0) {
                            aliaReal = aliaReal.substring(0, aliaReal.length() - 1);
                        }
                        // alias.put(tableAlias, nowQueryTable);//sql22
                        //sql6
                        // alias.put(tableAlias, "");// just store alias
                        alias.get().put(tableAlias, aliaReal);
                    }
                    break;

                case HiveParser.TOK_SELEXPR:
                    if (ast.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
                        String column = ast.getChild(0).getChild(0).getText().toLowerCase();
                        if (nowQueryTable.get().indexOf("&") > 0) {
                            // cols.get().put(UNKNOWN + "." + column, "");
                        } else if (colAlias.get().get(nowQueryTable.get() + "." + column) == null) {
                            cols.get().put(nowQueryTable.get() + "." + column, "");
                        }
                    } else if (ast.getChild(1) != null) {
                        // TOK_SELEXPR (+
                        // (TOK_TABLE_OR_COL id)
                        // 1) dd
                        String columnAlia = ast.getChild(1).getText().toLowerCase();
                        colAlias.get().put(nowQueryTable.get() + "." + columnAlia, "");
                    }
                    break;
                case HiveParser.TOK_UPSET_ELEMENT:
                    // 更新语句，比如TOK_UPSET_ELEMENT column_1 '1'
                    cols.get().put(ast.getChild(0).getText().toLowerCase(), "");
                    break;
                case HiveParser.TOK_WHERE:
                    // 条件，比如 TOK_WHERE (= (TOK_TABLE_OR_COL column_3) '3'
                    break;
                case HiveParser.DOT:
                    if (ast.getType() == HiveParser.DOT) {
                        if (ast.getChildCount() == 2) {
                            if (ast.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL
                                    && ast.getChild(0).getChildCount() == 1
                                    && ast.getChild(1).getType() == HiveParser.Identifier) {
                                String alia = BaseSemanticAnalyzer
                                        .unescapeIdentifier(ast.getChild(0).getChild(0).getText().toLowerCase());
                                String column = BaseSemanticAnalyzer
                                        .unescapeIdentifier(ast.getChild(1).getText().toLowerCase());
                                String realTable = null;
                                if (!tables.get().contains(alia + "\t" + nowSqlType) && alias.get().get(alia) == null) {
                                    alias.get().put(alia, nowQueryTable.get());
                                }
                                if (tables.get().contains(alia + "\t" + nowSqlType)) {
                                    realTable = alia;
                                } else if (alias.get().get(alia) != null) {
                                    realTable = alias.get().get(alia);
                                }
                                if (realTable == null || realTable.length() == 0 || realTable.indexOf("&") > 0) {
                                    // realTable = UNKNOWN;
                                    break;
                                }
                                cols.get().put(realTable + "." + column, "");

                            }
                        }
                    }
                    break;
                case HiveParser.TOK_ALTERTABLE_ADDPARTS:
                case HiveParser.TOK_ALTERTABLE_RENAME:
                case HiveParser.TOK_ALTERTABLE_ADDCOLS:
                    ASTNode alterTableName = (ASTNode) ast.getChild(0);
                    tables.get().add(alterTableName.getText() + "\t" + nowSqlType);
                    break;
                case HiveParser.TOK_TABCOLNAME:
                    // insert into table table1(a,b,c) TOK_TABCOLNAME的子节点就会有a,b,c
                    // 问题在于深度优先遍历时，还没走到TAK_TAB节点，因此读不到desc_table
                    // 格式参考：(TOK_INSERT_INTO (TOK_TAB (TOK_TABNAME desc_table) (TOK_TABCOLNAME a b c))
                    for (int i = 0; i < ast.getChildCount(); i++) {
                        String col = ast.getChild(i).getText().toLowerCase();
                        // cols.get().put(nowQueryTable.get() + "." + col, "");
                        cols.get().put(col, "");
                    }
                    break;

                default:
                    break;
            }
        }
        return set;
    }

    static String parseDbAndTable(ASTNode tabTree) {
        String tableName = ParseUtils.getUnescapedName((ASTNode) tabTree.getChild(0));
        if (tabTree.getChildCount() == 1) {
            databases.get().add(DEFAULT_DB);
            tableName = DEFAULT_DB + "." + tableName;
        } else {
            databases.get().add(tableName);
            tableName = tableName + "." + tabTree.getChild(1);
        }
        return tableName;
    }

    private static void endParseCurrentNode(ASTNode ast) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                //join 从句结束，跳出join
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_JOIN:
                    joinClause.set(false);
                    break;
                case HiveParser.TOK_QUERY:
                    break;
                case HiveParser.TOK_INSERT:
                case HiveParser.TOK_SELECT:
                    nowQueryTable.set(tableNameStack.get().pop());
                    nowSqlType.set(sqlTypeStack.get().pop());
                    break;
                default:
                    break;
            }
        }
    }


    public static String unescapeIdentifier(String val) {
        if (val == null) {
            return null;
        }
        if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    private static void output(Map<String, String> map) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + "\t" + map.get(key));
        }
    }

}
