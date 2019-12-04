package com.lhldyf.gallery.java.antlr.tw;

/**
 * @author lhldyf
 * @date 2019-11-28 15:30
 */
public interface SqlParseResult {

    /**
     * SQL类型， 如果是权限类，返回HivePermitParseInfo，SQL类返回HiveSqlParseInfo
     * @return
     */
    String getSqlType();

}
