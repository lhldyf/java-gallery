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
        System.out.println("360731199307177333".getBytes().length);
        System.out.println("3607311993071773331".getBytes().length);
        // selectTest();
        // insertTest();
        // updateTest();
        // deleteTest();
        // dropTest();
        // truncateTest();
        // grantTest();
        // revokeTest();
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
        parseHiveSql("revoke all on database spark from user mllib");
        parseHiveSql("REVOKE FACL ON TABLE alice_t1 FROM USER bob; ");

    }

    private static void grantTest() {
        parseHiveSql("grant select,insert,delete on cm.dw_sm_trlg to user omds");
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
        // parseHiveSql("insert into table db1.desc_table select alia1.col_1, alia2.col_2 from table1 alia1, table2 "
        //                      + "alia2 where alia1.cond1 = alia2.cond2");
        // parseHiveSql("insert into table db1.desc_table select * from db2.source_table");
        // parseHiveSql("insert into table desc_table(a,b,c) values(1,2,3)");
        parseHiveSql("insert into omds_dsbp_translist_tb_hb values (struct('begndt',date'2018-05-17','task_id',"
                             + "'170728571100100419900002','begin_orgno','5711001','trans_date','2018-05-17'),"
                             + "date'2018-05-17',date'2018-05-18','170728571100100419900002',"
                             + "'a666e8df95eabe448cdbb3e0e63ab7c0','170728571100100419900002',"
                             + "'20180517_652_470_2a3231b8-7ea8-f893-d970-a4b1461e7e99-62','null','004199','004199',"
                             + "'5711001','2018-05-17','20180517145906','20180522142954','430248','0019988','0019988',"
                             + "'null','null','2','zh0007','zh0007','701014','null','null','null','null','null','null',"
                             + "'null','null','0','0','null','null','null','201805')");
        // parseHiveSql("insert into table desc_table values(1,2,3)");
        // parseHiveSql("INSERT INTO default.wal_test SELECT NAMED_STRUCT('table_name','table_name-29350','etl_time',"
        //                      + "systimestamp,'data_source_system','data_source_system-29350'), 'table_name-29350',
        // CAST"
        //                      + "(TO_DATE(sysdate) AS DATE), systimestamp, 'TEST', 'data_source_system-29350', "
        //                      + "'testHbaseWAL', 342 FROM system.dual");
    }

    private static void selectTest() {
        // parseHiveSql("select * \n" + " --test\n" + "from table1  \n" + "--test");
        parseHiveSql("select t.a, b from t1 t where c > 999");
        parseHiveSql("insert into table t1 select * from t2");
        // parseHiveSql("select a.a1, b.b1 from a join b on a.a2 = b.b2");
        // parseHiveSql(
        //         "SELECT card_no,              pos_accum_consum_num,              pos_accum_consum_amt,              "
        //                 + "card_yavg_bal,              card_mavg_bal,              card_bal,              "
        //                 + "card_curr_deps_mavg_bal,              card_curr_deps_bal,              aum          FROM "
        //                 + "gdm.GDM_COMM_GT_CUSTOM_CARD_hs         WHERE Begndt >= '2019-08-30'           --AND
        // overdt"
        //                 + " < '2019-08-30'           AND Partid = Substr('2019-08-30', 1, 4) ||               Substr"
        //                 + "('2019-08-30', 6, 2)            AND open_branch IN (SELECT org_no from esm_org_level
        // where"
        //                 + " key_no LIKE '\"\"\"+ keyNo +\"\"\"%' ) limit 80000");
        parseHiveSql("select table_name as name, table_id as id, database_name, create_time, table_type, owner_name, "
                             + "commentstring, transactional, input_format, table_format, table_location, "
                             + "row_permission, "
                             + "column_permission, hbase_name, field_delim, line_delim, collection_delim, null as "
                             + "origin_text, null as expanded_text, false as is_temporary_table, 'table' as "
                             + "object_type "
                             + "from system.tables_v where database_name='omds' order by name union all (select "
                             + "table_name"
                             + " as name, -1 as id, database_name, create_time, table_type, owner_name, null as "
                             + "commentstring, null as transactional, null as input_format, table_format, "
                             + "table_location, "
                             + "row_permission, column_permission, null as hbase_name, field_delim, line_delim, "
                             + "collection_delim, null as origin_text, null as expanded_text, true as "
                             + "is_temporary_table, "
                             + "'table' as object_type from system.temporary_tables_v where database_name='omds' "
                             + "order by "
                             + "name) union all (select view_name as name, view_id as id, database_name, timestamp"
                             + "('1970-01-01 0:0:0') as create_time, null as table_type, owner_name as owner_name, "
                             + "null as"
                             + " commentstring, null as transactional, null as input_format, null as table_format, "
                             + "null as"
                             + " table_location, null as row_permission, null as column_permission, null as "
                             + "hbase_name, "
                             + "null as field_delim, null as line_delim, null as collection_delim, origin_text, "
                             + "expanded_text, false as is_temporary_table, 'view' as object_type from system.views_v "
                             + "where database_name='omds' order by name)");
        parseHiveSql("");
        // parseHiveSql("select alia1.col_1, alia2.col_2 from table1 alia1, table2 alia2 where alia1.cond1 = alia2
        // .cond2");
        // parseHiveSql(
        //         "select col_1, col_2 from table1 where col3 = '1' union select col_1, col_2 from table2 where
        // col3=2");
        parseHiveSql("select `t1`.*,`t1`.`col1` from `table1` `t1` limit 10");
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
