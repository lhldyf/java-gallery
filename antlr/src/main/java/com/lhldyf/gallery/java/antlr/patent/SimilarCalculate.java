package com.lhldyf.gallery.java.antlr.patent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lhldyf
 * @date 2020-02-28 10:50
 */
public class SimilarCalculate {
    public static void main(String[] args) {
        initWeight();
        // calculate(initNode1(), initNode2());
        // calculate(initNode1(), initNode3());
        calculate(initNode3(), initNode4());
    }

    private static BigDecimal calculate(Node node1, Node node2) {
        // 类型相同
        BigDecimal node1score = totalWeight(node1);
        System.out.println("树1权重和：" + node1score.toString());
        BigDecimal node2score = totalWeight(node2);
        System.out.println("树2权重和：" + node2score.toString());
        BigDecimal score = calculateScore(node1, node2, 1);
        System.out.println("树相似分：" + score.toString());
        BigDecimal sim = divide(score, node1score.add(node2score));
        System.out.println("树相似度：" + sim.toString());
        return score;
    }

    private static BigDecimal calculateScore(Node node1, Node node2, int depth) {
        BigDecimal score = new BigDecimal(0);
        if (node1.equals(node2)) {
            int temp = weight(node1) + weight(node2);
            BigDecimal factorScore = divide(temp, depth);

            System.out.println(
                    (null == node1.getScope() ? "" : node1.getScope() + "-") + node1.getType() + ":" + node1.getLabel()
                            + ", 深度为" + depth + "的节点相同，节点权重分为" + weight(node1) + "/" + depth + " = " + divide(
                            weight(node1), depth));
            score = score.add(factorScore);
            for (Node node1child : node1.getChildren()) {
                if (node1.visited)
                    continue;
                for (Node node2child : node2.getChildren()) {
                    if (node2.visited)
                        continue;
                    score = score.add(calculateScore(node1child, node2child, depth + 1));
                }
            }
        }
        return score;
    }

    private static BigDecimal divide(int a, int b) {
        return divide(new BigDecimal(a), new BigDecimal(b));
    }

    private static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal totalWeight(Node node) {
        return totalWeight(node, 1);
    }

    private static BigDecimal totalWeight(Node node, int depth) {
        BigDecimal total = new BigDecimal(0);
        total = total.add(divide(weight(node), depth));
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                total = total.add(totalWeight(child, depth + 1));
            }
        }
        return total;
    }

    private static Map<Node.NodeType, Map<Node.NodeScope, Integer>> map = new HashMap<>();

    static void initWeight() {
        initWeight(Node.NodeType.field, Node.NodeScope.update, 40);
    }

    private static void initWeight(Node.NodeType type, Node.NodeScope scope, Integer weight) {
        Map<Node.NodeScope, Integer> scopeMap = map.getOrDefault(type, new HashMap<>(16));
        scopeMap.put(scope, weight);
        map.put(type, scopeMap);
    }

    private static int DEFAULT_WEIGHT = 5;

    private static int weight(Node node) {
        if (null != map.get(node.getType())) {
            return map.get(node.getType()).getOrDefault(node.getScope(), DEFAULT_WEIGHT);
        }
        return DEFAULT_WEIGHT;
    }

    private static Node initNode1() {
        // select c1, c2 from t1 where c3 = '0';
        System.out.println("树1 sql: select t.a, b from t1 t where c > 999");
        Node root = initOptNode("select");
        Node t1 = initTableNode(Node.NodeScope.select, "t1");
        Node a = initFieldNode(Node.NodeScope.select, "a");
        Node b = initFieldNode(Node.NodeScope.select, "b");
        Node c = initFieldNode(Node.NodeScope.where, "c");
        Node f = initFunctionNode(">");
        Node v = initVariableNode("");
        f.setChildren(Collections.singletonList(v));
        c.setChildren(Collections.singletonList(f));
        t1.setChildren(Arrays.asList(a, b, c));
        root.setChildren(Collections.singletonList(t1));
        return root;
    }

    private static Node initNode2() {
        // select c1, c2 from t1 where c3 = '0';
        System.out.println("树2 sql: select b, a from t where c > 0");
        Node root = initOptNode("select");
        Node t1 = initTableNode(Node.NodeScope.select, "t1");
        Node a = initFieldNode(Node.NodeScope.select, "a");
        Node b = initFieldNode(Node.NodeScope.select, "b");
        Node c = initFieldNode(Node.NodeScope.where, "c");
        Node f = initFunctionNode(">");
        Node v = initVariableNode("");
        f.setChildren(Collections.singletonList(v));
        c.setChildren(Collections.singletonList(f));
        t1.setChildren(Arrays.asList(a, b, c));
        root.setChildren(Collections.singletonList(t1));
        return root;
    }


    private static Node initNode3() {
        // select c1, c2 from t1 where c3 = '0';
        System.out.println("树3 sql: update t1 set a = 1, b= 2 where c = 3 and d = 4");
        Node root = initOptNode("update");
        Node t1 = initTableNode(Node.NodeScope.select, "t1");
        Node a = initFieldNode(Node.NodeScope.update, "a");
        Node b = initFieldNode(Node.NodeScope.update, "b");
        Node c = initFieldNode(Node.NodeScope.where, "c");
        Node d = initFieldNode(Node.NodeScope.where, "d");
        Node af = initFunctionNode("=");
        Node bf = initFunctionNode("=");
        Node cf = initFunctionNode("=");
        Node df = initFunctionNode("=");
        Node av = initVariableNode("1");
        Node bv = initVariableNode("2");
        Node cv = initVariableNode("3");
        Node dv = initVariableNode("4");
        af.setChildren(Collections.singletonList(av));
        a.setChildren(Collections.singletonList(af));
        bf.setChildren(Collections.singletonList(bv));
        b.setChildren(Collections.singletonList(bf));
        cf.setChildren(Collections.singletonList(cv));
        c.setChildren(Collections.singletonList(cf));
        df.setChildren(Collections.singletonList(dv));
        d.setChildren(Collections.singletonList(df));
        t1.setChildren(Arrays.asList(a, b, c, d));
        root.setChildren(Collections.singletonList(t1));
        return root;
    }


    private static Node initNode4() {
        System.out.println("树4 sql: update t1 set a = 1, c= 2 where b = 3 and d = 4");
        Node root = initOptNode("update");
        Node t1 = initTableNode(Node.NodeScope.select, "t1");
        Node a = initFieldNode(Node.NodeScope.update, "a");
        Node b = initFieldNode(Node.NodeScope.where, "b");
        Node c = initFieldNode(Node.NodeScope.update, "c");
        Node d = initFieldNode(Node.NodeScope.where, "d");
        Node af = initFunctionNode("=");
        Node bf = initFunctionNode("=");
        Node cf = initFunctionNode("=");
        Node df = initFunctionNode("=");
        Node av = initVariableNode("1");
        Node bv = initVariableNode("3");
        Node cv = initVariableNode("2");
        Node dv = initVariableNode("4");
        af.setChildren(Collections.singletonList(av));
        a.setChildren(Collections.singletonList(af));
        bf.setChildren(Collections.singletonList(bv));
        b.setChildren(Collections.singletonList(bf));
        cf.setChildren(Collections.singletonList(cv));
        c.setChildren(Collections.singletonList(cf));
        df.setChildren(Collections.singletonList(dv));
        d.setChildren(Collections.singletonList(df));
        t1.setChildren(Arrays.asList(a, b, c, d));
        root.setChildren(Collections.singletonList(t1));
        return root;
    }

    private static Node initOptNode(String label) {
        return new Node(Node.NodeType.opt, null, label);
    }


    private static Node initTableNode(Node.NodeScope scope, String label) {
        return new Node(Node.NodeType.table, scope, label);
    }

    private static Node initFieldNode(Node.NodeScope scope, String label) {
        return new Node(Node.NodeType.field, scope, label);
    }

    private static Node initVariableNode(String label) {
        return new Node(Node.NodeType.variable, null, label);
    }

    private static Node initFunctionNode(String label) {
        return new Node(Node.NodeType.function, null, label);
    }

    private static Node initOperatorNode(String label) {
        return new Node(Node.NodeType.operator, null, label);
    }

    private static Node initSubQueryNode(String label) {
        return new Node(Node.NodeType.subQuery, null, label);
    }

}
