package ru.progwards.java2.lessons.graph;

import java.util.*;

public class Boruvka {
    // множество trees хранит корневые элементы деревьев
    private static Set<TreeNode> trees;
    // nodeMap: ключ - node, значение - TreeNode, соответствующий узлу node
    private static Map<Node, TreeNode> nodeMap;
    // edges: ребра остовного дерева
    private static List<Edge> edges;

    static List<Edge> minTree(Graph graph) {
        initialize(graph);
        callBoruvkaAlgorithm();
        return edges;
    }

    // forest initialization
    private static void initialize(Graph graph) {
        trees = new HashSet<>();
        nodeMap = new HashMap<>();
        edges = new ArrayList<>();
        for (Node node : (List<Node>) graph.nodes) {
            TreeNode tree = new TreeNode(node);
            trees.add(tree);
            nodeMap.put(node, tree);
        }
    }

    private static void callBoruvkaAlgorithm() {
        Iterator<TreeNode> treeIterator = trees.iterator();
        while (treeIterator.hasNext()) {
            TreeNode tree = treeIterator.next();
            Edge minEdge = null; // находим ребро с минимальным весом minEdge, исходящее из tree
            TreeNode secondTreeToBeMerged = null;
            TreeNode treeRoot = tree; // корневой элемент дерева
            while (tree != null) {  // проходим по всем узлам дерева
                for (Edge edge : (List<Edge>) tree.node.out) {
                    if (minEdge == null || minEdge.weight > edge.weight) {
                        TreeNode secondTree = nodeMap.get(edge.in);
                        TreeNode secondTreeRoot = secondTree.getMasterNode();
                        if (secondTreeRoot != treeRoot) {
                            minEdge = edge;
                            secondTreeToBeMerged = secondTree;
                        }
                    }
                }
                for (Edge edge : (List<Edge>) tree.node.in) {
                    if (minEdge == null || minEdge.weight > edge.weight) {
                        TreeNode secondTree = nodeMap.get(edge.out);
                        TreeNode secondTreeRoot = secondTree.getMasterNode();
                        if (secondTreeRoot != treeRoot) {
                            minEdge = edge;
                            secondTreeToBeMerged = secondTree;
                        }
                    }
                }
                tree = tree.getChild();
            }
            if (minEdge != null) {
                secondTreeToBeMerged.merge(treeRoot);
                treeIterator.remove();
                edges.add(minEdge);
            }
        }
    }

    private static class TreeNode {
        Node node;
        TreeNode parent;
        TreeNode child;

        TreeNode(Node node) {
            this.node = node;
        }

        TreeNode getParent() {
            return parent;
        }

        void setParent(TreeNode parent) {
            this.parent = parent;
        }

        TreeNode getChild() {
            return child;
        }

        void setChild(TreeNode child) {
            this.child = child;
        }

        TreeNode getMasterNode() {
            TreeNode treeNode = this;
            TreeNode nodeParent = treeNode.getParent();
            while (nodeParent != null) {
                treeNode = nodeParent;
                nodeParent = treeNode.getParent();
            }
            return treeNode;
        }

        TreeNode getTailNode() {
            TreeNode treeNode = this;
            TreeNode nodeChild = treeNode.getChild();
            while (nodeChild != null) {
                treeNode = nodeChild;
                nodeChild = treeNode.getChild();
            }
            return treeNode;
        }

        void merge(TreeNode treeNode) {
            TreeNode tailOfThis = this.getTailNode();
            treeNode.setParent(tailOfThis);
            tailOfThis.setChild(treeNode);
        }
    }

    public static void main(String[] args) {
        Node<String, String> nodeA = new Node<>();
        nodeA.info = "A";
        Node<String, String> nodeB = new Node<>();
        nodeB.info = "B";
        Node<String, String> nodeC = new Node<>();
        nodeC.info = "C";
        Node<String, String> nodeD = new Node<>();
        nodeD.info = "D";
        Node<String, String> nodeE = new Node<>();
        nodeE.info = "E";
        Node<String, String> nodeF = new Node<>();
        nodeF.info = "F";
        Node<String, String> nodeG = new Node<>();
        nodeG.info = "G";

        Edge<String, String> edgeAB = new Edge<>();
        edgeAB.info = "AB";
        edgeAB.weight = 7;
        edgeAB.in = nodeB;
        edgeAB.out = nodeA;
        Edge<String, String> edgeBC = new Edge<>();
        edgeBC.info = "BC";
        edgeBC.weight = 8;
        edgeBC.in = nodeB;
        edgeBC.out = nodeC;
        Edge<String, String> edgeAD = new Edge<>();
        edgeAD.info = "AD";
        edgeAD.weight = 5;
        edgeAD.in = nodeD;
        edgeAD.out = nodeA;
        Edge<String, String> edgeBD = new Edge<>();
        edgeBD.info = "BD";
        edgeBD.weight = 9;
        edgeBD.in = nodeB;
        edgeBD.out = nodeD;
        Edge<String, String> edgeBE = new Edge<>();
        edgeBE.info = "BE";
        edgeBE.weight = 7;
        edgeBE.in = nodeB;
        edgeBE.out = nodeE;
        Edge<String, String> edgeCE = new Edge<>();
        edgeCE.info = "CE";
        edgeCE.weight = 5;
        edgeCE.in = nodeE;
        edgeCE.out = nodeC;
        Edge<String, String> edgeDE = new Edge<>();
        edgeDE.info = "DE";
        edgeDE.weight = 15;
        edgeDE.in = nodeD;
        edgeDE.out = nodeE;
        Edge<String, String> edgeDF = new Edge<>();
        edgeDF.info = "DF";
        edgeDF.weight = 6;
        edgeDF.in = nodeD;
        edgeDF.out = nodeF;
        Edge<String, String> edgeFE = new Edge<>();
        edgeFE.info = "FE";
        edgeFE.weight = 8;
        edgeFE.in = nodeE;
        edgeFE.out = nodeF;
        Edge<String, String> edgeEG = new Edge<>();
        edgeEG.info = "EG";
        edgeEG.weight = 9;
        edgeEG.in = nodeE;
        edgeEG.out = nodeG;
        Edge<String, String> edgeFG = new Edge<>();
        edgeFG.info = "FG";
        edgeFG.weight = 11;
        edgeFG.in = nodeF;
        edgeFG.out = nodeG;

        nodeA.out.add(edgeAD);
        nodeA.out.add(edgeAB);
        nodeB.in.add(edgeAB);
        nodeB.in.add(edgeBC);
        nodeB.in.add(edgeBE);
        nodeB.in.add(edgeBD);
        nodeC.out.add(edgeBC);
        nodeC.out.add(edgeCE);
        nodeD.in.add(edgeAD);
        nodeD.out.add(edgeBD);
        nodeD.in.add(edgeDE);
        nodeD.in.add(edgeDF);
        nodeE.in.add(edgeCE);
        nodeE.out.add(edgeBE);
        nodeE.out.add(edgeDE);
        nodeE.in.add(edgeFE);
        nodeE.in.add(edgeEG);
        nodeF.out.add(edgeDF);
        nodeF.out.add(edgeFE);
        nodeF.in.add(edgeFG);
        nodeG.out.add(edgeFG);
        nodeG.out.add(edgeEG);
        Graph<String, String> graph = new Graph<>();
        graph.nodes.addAll(Set.of(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG));
        graph.edges.addAll(Set.of(edgeAB, edgeBC, edgeAD, edgeBD, edgeBE, edgeCE, edgeDE, edgeDF, edgeFE, edgeFG, edgeEG));

        List<Edge> edges = Boruvka.minTree(graph);
        edges.forEach(System.out::println);
        System.out.println(edges.contains(edgeAD) + "; " + edges.contains(edgeCE) + "; " + edges.contains(edgeDF)
                + "; " + edges.contains(edgeAB) + "; " + edges.contains(edgeBE) + "; " + edges.contains(edgeEG));
    }
}
