package com.lhldyf.gallery.java.druid;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

import java.util.List;

/**
 * https://github.com/alibaba/druid/blob/master/src/test/java/com/alibaba/druid/TestUtil.java
 * @author lhldyf
 * @date 2019-11-05 8:58
 */
public class SqlParseTest {

    public static void main(String[] args) throws Exception {
        SqlParseTest test = new SqlParseTest();
        test.setUp();
        // test.test_pert();
        System.out.println(test.execHive(test.sql));
        // System.out.println(test.execMySql(test.sql));
    }

    private String sql;

    protected void setUp() throws Exception {
        sql = "SELECT * FROM T";
        sql = "SELECT ID, NAME, AGE FROM USER WHERE ID = ?";
        sql = "SELECT * FROM system.databases_v ORDER BY database_name";
        sql = "SELECT e.last_name,\n" + "       e.department_id,\n" + "       d.department_name\n"
                + "FROM   employees e\n" + "       LEFT OUTER JOIN department d\n"
                + "         ON ( e.department_id = d.department_id ); ";
        sql = "INSERT INTO T_1 SELECT * FROM T_2";
        sql = "SELECT table_name AS name, database_name, create_time, table_type, owner_name, commentstring, "
                + "transactional, table_format, table_location, NULL AS origin_text, NULL AS expanded_text, "
                + "'TABLE' AS object_type FROM system.tables_v WHERE database_name='system' ORDER BY name "
                + "UNION ALL SELECT view_name AS name, database_name, TIMESTAMP('1970-01-01 0:0:0') AS "
                + "create_time, NULL AS table_type, owner_name AS owner_name, NULL AS commentstring, NULL AS "
                + "transactional, NULL AS table_format, NULL AS table_location, origin_text, expanded_text, "
                + "'VIEW' AS object_type FROM system.views_v WHERE database_name=(select a from t1) ORDER BY name";
        sql = "select a.a1, b.b1 from t1 a, t2 b where a.a2 = b.b2";
        sql = "insert into a values(1,2,3)";
        // sql = "SELECT mask('1234567', '*', 2, 3) AS result FROM system.dual LIMIT 1";
        // sql = "INSERT INTO TABLE ta VALUES ('sssss','44')";

        //        sql = Utils.readFromResource("benchmark/sql/ob_sql.txt");
    }


    public void test_pert() throws Exception {
        for (int i = 0; i < 10; ++i) {
            perfMySql(sql);
        }
    }

    long perfMySql(String sql) {
        long startYGC = TestUtils.getYoungGC();
        long startYGCTime = TestUtils.getYoungGC();
        long startFGC = TestUtils.getFullGC();

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            execMySql(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;

        long ygc = TestUtils.getYoungGC() - startYGC;
        long ygct = TestUtils.getYoungGC() - startYGCTime;
        long fgc = TestUtils.getFullGC() - startFGC;

        System.out.println("MySql\t" + millis + ", ygc " + ygc + ", ygct " + ygct + ", fgc " + fgc);
        return millis;
    }

    protected String execHive(String sql) {
        StringBuilder out = new StringBuilder();
        HiveOutputVisitor visitor = new HiveOutputVisitor(out);
        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.println();
        }
        return out.toString();
    }

    protected String execMySql(String sql) {
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.println();
        }
        return out.toString();
    }

}
