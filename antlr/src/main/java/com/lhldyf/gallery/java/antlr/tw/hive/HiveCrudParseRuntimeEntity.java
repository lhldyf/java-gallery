package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import lombok.Data;

import java.util.*;

/**
 * Hive Crud的解析运行时
 * @author lhldyf
 * @date 2019-12-01 19:26
 */
@Data
public class HiveCrudParseRuntimeEntity implements HiveSqlParseRuntimeEntity {

    // 为多线程执行时创建ThreadLocal变量
    /** 表的别名 **/
    public Map<String, String> alias = new HashMap<>();
    /** 影响的列 **/
    private TreeMap<String, String> cols = new TreeMap<>();
    /** 列的别名 **/
    private TreeMap<String, String> colAlias = new TreeMap<>();
    /** 操作的表 **/
    private Set<String> tables = new HashSet<>();
    /** 操作的库 **/
    private Set<String> databases = new HashSet<>();
    /** 表名的栈 **/
    private Stack<String> tableNameStack = new Stack<>();
    /** 操作的栈 **/
    private Stack<String> sqlTypeStack = new Stack<>();
    /**
     * 定义及处理不清晰，修改为query或from节点对应的table集合或许好点。目前正在查询处理的表可能不止一个。
     */
    private String nowQueryTable = "";
    /** 当前正在识别的操作 **/
    private String nowSqlType = "";
    /** 当前是否是join从句 **/
    private Boolean joinClause = false;

    /** 本次操作的操作类型 **/
    private String sqlType;

    // 以下为重构后的数据结构

    /** Map<库, Map<表, Set<列>>> **/
    private Map<String, Map<String, Set<String>>> affectMap = new HashMap<>();

    /** Map<库, Map<表, Set<列>>> **/
    private Map<String, Map<String, Set<String>>> relatedMap = new HashMap<>();

    /** 别名的Map<别名, 库.表> **/
    private Map<String, String[]> aliasMap = new HashMap<>();

    /** 目前是否是where子句, 如果是where子句，这个里面的内容只是related，而不是affect **/
    private boolean whereClause = false;

    /** 是否where子句的栈 **/
    private Stack<Boolean> whereClauseStack = new Stack<>();

    /** 当前子句是否是affect操作 **/
    private boolean affectClause = false;

    /** 当前子句是否是affect操作的栈 **/
    private Stack<Boolean> affectClauseStack = new Stack<>();

    /** 当前正在操作的表，形式为`库名.表名`，若关联查询必须使用别名，否则该值为unknown.unknown **/
    private String[] currDbTable = SqlParseConstant.UNKNOWN_TABLE;

    /** 当前正在操作的表的栈 **/
    private Stack<String[]> currTableStack = new Stack<>();

    /** limit 行数限制 在Select场景下生效 **/
    private int limitCnt;

    /** delete update 是否有过滤器 **/
    private boolean hasFilter;
}
