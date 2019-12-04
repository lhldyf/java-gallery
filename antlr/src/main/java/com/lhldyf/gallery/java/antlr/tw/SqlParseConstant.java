package com.lhldyf.gallery.java.antlr.tw;

import java.util.HashSet;
import java.util.Set;

/**
 * 目前工具类支持的Hive SQL 解析类型
 * @author lhldyf
 * @date 2019-12-01 18:34
 */
public class SqlParseConstant {
    public static final String SQL_TYPE_INSERT = "INSERT";
    public static final String SQL_TYPE_UPDATE = "UPDATE";
    public static final String SQL_TYPE_DELETE = "DELETE";
    public static final String SQL_TYPE_SELECT = "SELECT";
    public static final String SQL_TYPE_DROP = "DROP";
    public static final String SQL_TYPE_TRUNCATE = "TRUNCATE";
    public static final String SQL_TYPE_GRANT = "GRANT";
    public static final String SQL_TYPE_REVOKE = "REVOKE";

    public static final String PAYLOAD_TYPE_DB = "database";
    public static final String PAYLOAD_TYPE_TABLE = "table";
    public static final String PAYLOAD_TYPE_ROLE = "role";

    public static final String PERMIT_TYPE_CREATE = "create";
    public static final String PERMIT_TYPE_UPDATE = "update";
    public static final String PERMIT_TYPE_DELETE = "delete";
    public static final String PERMIT_TYPE_SELECT = "select";
    public static final String PERMIT_TYPE_ALL = "all";

    public static final String[] UNKNOWN_TABLE = new String[] {"unknown", "unknown"};
    public static final String DOT = ".";
    public static final String COLUMN_ALL = "*";

    public static Set<String> SQL_TYPE_SET = new HashSet<>();
    static {
        SQL_TYPE_SET.add(SQL_TYPE_INSERT);
        SQL_TYPE_SET.add(SQL_TYPE_UPDATE);
        SQL_TYPE_SET.add(SQL_TYPE_DELETE);
        SQL_TYPE_SET.add(SQL_TYPE_SELECT);
        SQL_TYPE_SET.add(SQL_TYPE_GRANT);
        SQL_TYPE_SET.add(SQL_TYPE_REVOKE);
        SQL_TYPE_SET.add(SQL_TYPE_DROP);
        SQL_TYPE_SET.add(SQL_TYPE_TRUNCATE);
    }
}
