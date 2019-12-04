package com.lhldyf.gallery.java.antlr;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.hadoop.hive.ql.parse.HiveParser.*;

/**
 * @author chentiefeng
 * @date 2019/10/21 13:51
 */
@Slf4j
public class HiveSqlParse {

    private HiveConf conf = new HiveConf();
    private ParseDriver pd = new ParseDriver();
    /**
     * 原始表(表名,别名)
     */
    private List<String[]> sourceTable = Lists.newArrayList();
    /**
     * 插入表
     */
    private List<String> insertTables = Lists.newArrayList();
    /**
     * 最外层列
     */
    private List<String> outermostColumns = Lists.newArrayList();
    /**
     * 插入分区信息(分区列,分区值)
     */
    private Map<String, String> partitionMap = Maps.newHashMap();
    /**
     * 最外层Sel节点
     */
    private ASTNode outermostSelNode = null;
    /**
     * 最外层Insert节点
     */
    private ASTNode outermostInsertNode = null;
    /**
     * 放置 解析表栈
     */
    private Stack<HiveTableParseInfo> tableParseInfoSelStack = new Stack<>();
    private Stack<HiveTableParseInfo> tableParseInfoFromStack = new Stack<>();
    /**
     * 表关系解析信息，不包含原始表
     */
    private HiveTableParseInfo tableParseInfo = null;

    public HiveSqlParse() {
    }

    public HiveSqlParse(String sql) {
        parse(sql);
    }

    /**
     * sql解析
     * @param sql
     */
    public void parse(String sql) {
        try {
            ASTNode ast = pd.parse(sql, null, conf);
            log.info("hiveSql={},astTree={}", sql, ast.toStringTree());
            parseNode(ast);
            insert(outermostInsertNode);
            outermostColumns(outermostSelNode);
            sourceTable.removeIf(arr -> arr[0].equals(insertTables.get(0)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void parseNode(ASTNode ast) {
        if (CollectionUtils.isNotEmpty(ast.getChildren())) {
            for (Node child : ast.getChildren()) {
                ASTNode cc = (ASTNode) child;
                switch (cc.getToken().getType()) {
                    case TOK_INSERT:
                        outermostInsertNode = cc;
                        break;
                    case TOK_TABNAME:
                        String tableName = Joiner.on(".")
                                                 .join(cc.getChildren().stream().map(n -> ((ASTNode) n).getText())
                                                         .collect(Collectors.toList()));
                        ASTNode ccChild = (ASTNode) cc.getParent().getChild(cc.getParent().getChildCount() - 1);
                        HiveTableParseInfo sourceTableParseInfo = new HiveTableParseInfo();
                        if (ccChild.getToken().getType() == TOK_TABNAME) {
                            sourceTable.add(new String[] {tableName, ""});
                            sourceTableParseInfo.setAlias("");
                        } else {
                            sourceTable.add(new String[] {tableName, ccChild.getText()});
                            sourceTableParseInfo.setAlias(ccChild.getText());
                        }
                        sourceTableParseInfo.setName(tableName);
                        if (!tableParseInfoFromStack.empty()) {
                            tableParseInfoFromStack.pop().getTables().add(sourceTableParseInfo);
                        }
                        break;
                    case TOK_QUERY:
                        ASTNode ccc = (ASTNode) cc.getParent().getChild(cc.getParent().getChildCount() - 1);
                        if (ccc.getToken().getType() != TOK_QUERY) {
                            HiveTableParseInfo table = new HiveTableParseInfo();
                            table.setAlias(ccc.getText());
                            tableParseInfoSelStack.push(table);
                            tableParseInfoFromStack.push(table);
                        }
                        break;
                    case TOK_SELECT:
                    case TOK_SELECTDI:
                        HiveTableParseInfo pop = tableParseInfoSelStack.pop();
                        if (!tableParseInfoSelStack.empty()) {
                            HiveTableParseInfo father = tableParseInfoSelStack.peek();
                            if (Objects.nonNull(father)) {
                                father.getTables().add(pop);
                            }
                        } else {
                            tableParseInfo = pop;
                        }
                        parseColumns(cc, pop);
                        continue;
                    default:
                }
                parseNode(cc);
            }
        }
    }

    private void insert(ASTNode cn) {
        if (CollectionUtils.isEmpty(cn.getChildren())) {
            return;
        }
        for (Node child : cn.getChildren()) {
            ASTNode cc = (ASTNode) child;
            switch (cc.getToken().getType()) {
                case TOK_INSERT_INTO:
                case TOK_DESTINATION:
                    insertTable(cn);
                    continue;
                case TOK_SELECT:
                    outermostSelNode = cn;
                    continue;
                default:
            }
            insert(cc);
        }
    }

    private void parseColumns(ASTNode cc, HiveTableParseInfo table) {
        for (Node node : cc.getChildren()) {
            ASTNode tokSelExpr = (ASTNode) node;
            HiveTableParseInfo.HiveTableColumnParseInfo column = new HiveTableParseInfo.HiveTableColumnParseInfo();
            String alias = getSelExprAlias(tokSelExpr);
            column.setName(alias);
            parseColumn(tokSelExpr, column);
            table.getColumns().add(column);
        }
    }


    private void parseColumn(ASTNode tokSelExpr, HiveTableParseInfo.HiveTableColumnParseInfo column) {
        if (CollectionUtils.isEmpty(tokSelExpr.getChildren())) {
            return;
        }
        for (Node child : tokSelExpr.getChildren()) {
            ASTNode cc = (ASTNode) child;
            if (cc.getToken().getType() == TOK_TABLE_OR_COL) {
                ASTNode ccc = (ASTNode) cc.getParent().getChild(cc.getParent().getChildCount() - 1);
                String[] item;
                if (ccc.getToken().getType() == TOK_TABLE_OR_COL) {
                    item = new String[] {cc.getChild(0).getText(), ""};
                } else {
                    item = new String[] {ccc.getText(), cc.getChild(0).getText()};
                }
                Optional<String[]> any = column.getSourceList().stream().filter(s -> Arrays.equals(item, s)).findAny();
                if (!any.isPresent()) {
                    column.getSourceList().add(item);
                }
                continue;
            }
            parseColumn(cc, column);
        }
    }

    /**
     * 插入信息
     * @param cn
     */
    private void insertTable(ASTNode cn) {
        if (CollectionUtils.isEmpty(cn.getChildren())) {
            return;
        }
        for (Node child : cn.getChildren()) {
            ASTNode cc = (ASTNode) child;
            switch (cc.getToken().getType()) {
                case TOK_TABNAME:
                    String tableName = Joiner.on(".").join(cc.getChildren().stream().map(n -> ((ASTNode) n).getText())
                                                             .collect(Collectors.toList()));
                    insertTables.add(tableName);
                    break;
                case TOK_PARTVAL:
                    if (cc.getChildCount() == 2) {
                        partitionMap.put(cc.getChild(0).getText(), cc.getChild(1).getText());
                    } else {
                        partitionMap.put(cc.getChild(0).getText(), null);
                    }
                    break;
                default:
            }
            insertTable(cc);
        }
    }

    /**
     * 最外层列
     * @param cn
     */
    private void outermostColumns(ASTNode cn) {
        if (CollectionUtils.isEmpty(cn.getChildren())) {
            return;
        }
        for (Node cnChild : cn.getChildren()) {
            ASTNode cc = (ASTNode) cnChild;
            if (cc.getToken().getType() == TOK_SELEXPR) {
                String alias = getSelExprAlias(cc);
                outermostColumns.add(alias);
                continue;
            }
            outermostColumns(cc);
        }
    }

    /**
     * 列别名获取
     * @param cc
     * @return
     */
    private String getSelExprAlias(ASTNode cc) {
        ASTNode child = (ASTNode) cc.getChild(cc.getChildCount() - 1);
        if (child.getToken().getType() == TOK_TABLE_OR_COL || child.getToken().getType() == DOT) {
            return child.getChild(child.getChildCount() - 1).getText();
        } else {
            return child.getText();
        }
    }

    public List<String> getOutermostColumns() {
        return outermostColumns;
    }

    public List<String> getSourceTables() {
        return sourceTable.stream().map(t -> t[0]).distinct().collect(Collectors.toList());
    }

    public String getInsertTable() {
        return CollectionUtils.isNotEmpty(insertTables) ? insertTables.get(0) : null;
    }

    public Map<String, String> getPartition() {
        return partitionMap;
    }

    public HiveTableParseInfo getTableParseInfo() {
        return tableParseInfo;
    }

    public static void main(String[] args) {
        String sql23 =
                "insert overwrite table risk_event partition(year='2019',dt) select t.ops as order_no,t.id_no , concat"
                        + "(t.consumer_no,'aa') dd,aadx from (select concat(a.opt_id,b.opt_id) as ops,b.id_no from "
                        + "ods.arc_event a left outer join ods.arc_user b on a.consumer_no = b.consumer_no) t left "
                        + "outer join (select order_no from arc_verify where dt = '20191023') t1 on t.consumer_no = "
                        + "t1.consumer_no";
        //        String sql23 = "insert overwrite table riskt_eventpartition select opt_id from arc_event a inner
        // join arc_user b";
        //        String sql23 = "insert overwrite table riskt_eventpartition select opt_id from arc_event";
        // String sql23 =
        //         "SQL_TYPE_SELECT SUM(CASE when rcw.eventid=2 and rcw.method = 'sendevent' then 1 else 0 END) as
        // successCnt,"
        //                 + "       SUM(CASE when rcw.eventid=4 and rcw.method = 'risklevel' then 1 else 0 END) as "
        //                 + "payCnt,"
        //                 + "       SUM(CASE when rcw.eventid=2 and rcw.method = 'sendevent' then 1 else 0 END)/SUM "
        //                 + "(CASE when rcw.eventid=4 and rcw.method = 'risklevel' then 1 else 0 END) as rate"
        //                 + "  FROM (\n" + "        SQL_TYPE_SELECT DISTINCT payorderid," + "               eventid,"
        //                 + "               method" + "          FROM log.pay_rc_warden_event_basic"
        //                 + "         WHERE dt = '20180715'" + "       ) rcw";
        HiveSqlParse hiveSqlParse = new HiveSqlParse(sql23);
        System.out.println(hiveSqlParse.getSourceTables());
        System.out.println(hiveSqlParse.getOutermostColumns());
        System.out.println(hiveSqlParse.getInsertTable());
        System.out.println(hiveSqlParse.getPartition());
        System.out.println(hiveSqlParse.getTableParseInfo());
    }
}

