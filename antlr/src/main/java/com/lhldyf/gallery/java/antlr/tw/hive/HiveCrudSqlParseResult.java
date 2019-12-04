package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseResult;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Hive SQL解析结果的数据结构
 * @author lhldyf
 * @date 2019-11-25 18:32
 */
@ToString
@Data
public class HiveCrudSqlParseResult implements SqlParseResult {

    private String sqlType;

    /**
     * 影响的库列表
     */
    private List<String> affectDBList = new ArrayList<>();

    /**
     * 影响的表列表
     */
    private List<String> affectTableList = new ArrayList<>();

    /**
     * 影响的列列表
     */
    private List<String> affectColumnList = new ArrayList<>();

    /**
     * 关联的库列表
     */
    private List<String> relatedDBList = new ArrayList<>();
    /**
     * 关联的表列表
     */
    private List<String> relatedTableList = new ArrayList<>();
    /**
     * 关联的列列表
     */
    private List<String> relatedColumnList = new ArrayList<>();

    private int limitCnt;

    private boolean hasFilter;

}
