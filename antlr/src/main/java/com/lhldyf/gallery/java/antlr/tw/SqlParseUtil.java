package com.lhldyf.gallery.java.antlr.tw;

import com.lhldyf.gallery.java.antlr.tw.hive.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL 解析工具类
 * @author lhldyf
 * @date 2019-12-01 21:04
 */
@Slf4j
public class SqlParseUtil {

    public static Map<String, ISqlParser> hiveSqlParser = new HashMap<>();
    static {
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_SELECT, new HiveSelectSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_INSERT, new HiveInsertSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_UPDATE, new HiveUpdateSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_DELETE, new HiveDeleteSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_GRANT, new HiveGrantSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_REVOKE, new HiveGrantSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_DROP, new HiveDropSqlParser());
        hiveSqlParser.put(SqlParseConstant.SQL_TYPE_TRUNCATE, new HiveTruncateSqlParser());
    }

    public static void main(String[] args) {
        // selectTest();
        insertTest();
        // updateTest();
        // deleteTest();
        dropTest();
        truncateTest();
        grantTest();
        revokeTest();
    }

    private static void truncateTest() {
        // parseHiveSql("truncate table db1.table1");
    }

    private static void dropTest() {
        // parseHiveSql("drop table table1");
        // parseHiveSql("drop table db1.table1");
        // parseHiveSql("drop database db1");
    }

    private static void revokeTest() {
        // parseHiveSql("revoke all on database spark from user mllib");

    }

    private static void grantTest() {
        // parseHiveSql("grant role1  to role public");
        // parseHiveSql("GRANT r1,r2 TO USER user1, role role1");
        // parseHiveSql(
        //         "GRANT CREATE,UPDATE,DELETE,SELECT ON
        // DATABASE default TO user "
        //                 + "mllib");
        // parseHiveSql("grant create on *.* to role public, user user1");
        // parseHiveSql("GRANT ALL on table authorization_test to group test_role");
    }

    private static void deleteTest() {
        parseHiveSql("delete from delete_table where id1 in ( select id2 from where_table)");
        parseHiveSql("delete from delete_table t  where 1=1 and t.col1 = 2;");
        parseHiveSql("delete from delete_table t  where 1=1 ");
    }

    private static void updateTest() {
        parseHiveSql("update update_table t set t.column_1 = '1', t.column_2=2 where t.column_3 = '3'");
        parseHiveSql("update update_table set column_1 = '1', column_2=2 where 1=1");
        parseHiveSql(
                "update default.update_table t set t.column_1 = '1', t.column_2=(select a from table1) where column_3"
                        + " in " + "(select " + "where_colmun from db1.where_table)");
    }

    private static void insertTest() {
        parseHiveSql("insert into table db1.desc_table select alia1.col_1, alia2.col_2 from table1 alia1, table2 "
                             + "alia2 where alia1.cond1 = alia2.cond2");
        parseHiveSql("insert into table db1.desc_table select * from db2.source_table");
        parseHiveSql("insert into table desc_table(a,b,c) values(1,2,3)");
        parseHiveSql("insert into table desc_table values(1,2,3)");
        parseHiveSql("INSERT INTO default.wal_test SELECT NAMED_STRUCT('table_name','table_name-29350','etl_time',"
                             + "systimestamp,'data_source_system','data_source_system-29350'), 'table_name-29350', CAST"
                             + "(TO_DATE(sysdate) AS DATE), systimestamp, 'TEST', 'data_source_system-29350', "
                             + "'testHbaseWAL', 342 FROM system.dual");
    }

    private static void selectTest() {
        parseHiveSql("select t.a, t.* from (select a,b,c from table1) t");
        parseHiveSql("");
        // parseHiveSql("select alia1.col_1, alia2.col_2 from table1 alia1, table2 alia2 where alia1.cond1 = alia2
        // .cond2");
        // parseHiveSql(
        //         "select col_1, col_2 from table1 where col3 = '1' union select col_1, col_2 from table2 where
        // col3=2");
        // parseHiveSql("select t1.* from table1 t1 limit 10");
        // parseHiveSql("select t1.* from table1 t1 limit 10,20");
        // parseHiveSql("Select name,ip from zpc2 bieming where age > 10 and area in (select area from system.city)");
        // parseHiveSql("select col_1, col_2 from table1, table2 where col_3=col_4");
        // parseHiveSql("SELECT table_name AS name, database_name, create_time, table_type, owner_name, commentstring, "
        //                      + "transactional, table_format, table_location, NULL AS origin_text, NULL AS "
        //                      + "expanded_text, "
        //                      + "'TABLE' AS object_type FROM system.tables_v WHERE database_name='default' ORDER BY "
        //                      + "name "
        //                      + "UNION ALL SELECT view_name AS name, database_name, TIMESTAMP('1970-01-01 0:0:0') AS "
        //                      + "create_time, NULL AS table_type, owner_name AS owner_name, NULL AS commentstring, "
        //                      + "NULL AS " + "transactional, NULL AS table_format, NULL AS table_location,
        // origin_text, "
        //                      + "expanded_text, "
        //                      + "'VIEW' AS object_type FROM system.views_v WHERE database_name='default' ORDER BY
        // name");
    }

    public static SqlParseResult parseHiveSql(String sql) {
        String sqlType = parseSqlType(sql);
        if (null == sqlType || hiveSqlParser.get(sqlType) == null) {
            return null;
        }

        SqlParseResult result = hiveSqlParser.get(sqlType).parse(sql, sqlType);
        System.out.println(result);
        return result;
    }


    private static String parseSqlType(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        int index = sql.indexOf(" ");
        if (index < 1) {
            log.error("异常SQL: ", sql);
            return null;
        }

        String key = sql.substring(0, index).toUpperCase();
        if (SqlParseConstant.SQL_TYPE_SET.contains(key)) {
            return key;
        }

        log.warn("暂不支持该SQL的解析，SQL:{}", sql);
        return null;
    }

}
