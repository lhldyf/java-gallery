package com.lhldyf.gallery.java.antlr.patent;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lhldyf
 * @date 2020-02-28 9:44
 */
@Data
public class Node {


    /** 节点类型 **/
    private NodeType type;

    /** 节点操作域 **/
    private NodeScope scope;

    /** 节点标记名称 **/
    private String label;

    /** 节点权重 **/
    private int weight;

    /** 子节点 **/
    private List<Node> children = new ArrayList<>();

    public boolean visited = false;

    public Node(NodeType type, NodeScope scope, String label) {
        this.type = type;
        this.scope = scope;
        this.label = label;
    }

    public static enum NodeType {
        /** SQL操作类型 比如select/update/delete/insert **/
        opt,
        /** 表 **/
        table,
        /** 字段 **/
        field,
        /** 变量 **/
        variable,
        /** 函数 比如 max min **/
        function,
        /** 操作符，比如大于、小于、包含 等 **/
        operator,
        /** 子查询 **/
        subQuery;
    }


    public static enum NodeScope {
        /** 表or字段的新增 **/
        insert,
        /** 表的删除 **/
        delete,
        /** 表or字段的更新 **/
        update,
        /** 表or字段的查询 **/
        select,
        /** 被Join的表 **/
        selectJoin,
        /** 被Left Join的表 **/
        selectLeftJoin,
        /** 被Right Join的表 **/
        selectRightJoin,
        /** 被Full Join的表 **/
        selectFullJoin,
        /** 作为过滤条件的字段 **/
        where;
    }

    public boolean equals(Node node) {
        return this.type == node.type && this.scope == node.scope && Objects.equals(this.label, node.label);
    }
}
