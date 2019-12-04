package com.lhldyf.gallery.java.antlr.tw.hive;

/**
 * SQL解析运行时实体对象
 * @author lhldyf
 * @date 2019-12-01 17:18
 */
public interface HiveSqlParseRuntimeEntity {

    /**
     * 获取当前的SQL操作类型
     * @return
     */
    String getSqlType();
}
