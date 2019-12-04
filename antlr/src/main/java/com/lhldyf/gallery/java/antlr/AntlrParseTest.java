package com.lhldyf.gallery.java.antlr;

import org.antlr.runtime.ANTLRInputStream;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author lhldyf
 * @date 2019-11-25 10:06
 */
public class AntlrParseTest {
    public static void main(String[] args) throws Exception {
        String sql =
                "SQL_TYPE_INSERT INTO desttable SQL_TYPE_SELECT   aa , bb ,cc FROM log_alb_sum WHERE cc>1000  AND  "
                        + "bb=dd AND ss IS NULL" + " AND cc IS   NOT NULL";

        ParseDriver parseDriver = new ParseDriver();
        HiveConf hiveConf = new HiveConf();
        ASTNode astNode = parseDriver.parse(sql, null, hiveConf);
        System.out.println(astNode);

        HiveParse hp = new HiveParse();


        InputStream in = new ByteArrayInputStream(sql.getBytes());
        ANTLRInputStream input = new ANTLRInputStream(in);
        ParseDriver.ANTLRNoCaseStringStream antlrNoCaseStringStream = parseDriver.new ANTLRNoCaseStringStream(sql);
        ParseDriver.HiveLexerX hiveLexerX = parseDriver.new HiveLexerX();
        // HiveParser parser = new HiveParser(tokens);
        // parser.getTreeAdaptor();
    }
}
