package com.lhldyf.gallery.java.antlr;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lhldyf
 * @date 2019-11-25 11:47
 */
@Data
public class HiveTableParseInfo {

    private String name;

    private String alias;

    private List<HiveTableColumnParseInfo> columns = new ArrayList<>();

    private List<HiveTableParseInfo> tables = new ArrayList<>();


    @Data
    public static class HiveTableColumnParseInfo {
        private String name;

        private String alias;

        private List<String[]> sourceList = new ArrayList<>();
    }
}
