package com.lhldyf.gallery.java.antlr.tw;

/**
 * Hive SQL 解析
 * @author lhldyf
 * @date 2019-12-01 18:26
 */
public interface ISqlParser {

    /**
     * SQL解析出SQL实体对象
     * @param sql
     * @param sqlType
     * @return
     */
    SqlParseResult parse(String sql, String sqlType);
}
