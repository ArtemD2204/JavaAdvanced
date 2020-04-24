package ru.progwards.java2.lessons.graph;

import java.util.*;

// Алгоритм ищет минимальное остовное дерево в связанном неориентированном графе
public class BoruvkaV2<N, E> {
    static List<Edge> getSpanTree(Graph graph) {
        return new BoruvkaV2<>().getMinTree(graph);
    }

    // forest initialization
    private void initialize(Map<N, Node<N, E>> masterNodes, Map<N, Tree> trees, Graph<N, E> graph) {
        for (Node<N, E> node : graph.nodes) {
            masterNodes.put(node.info, node);
            Tree tree = new Tree();
            tree.nodes.add(node);
            tree.setMasterNode(node);
            trees.put(node.info, tree);
        }
    }

    private void callBoruvkaAlgorithm(Map<N, Node<N, E>> masterNodes, Map<N, Tree> trees, Graph<N, E> graph) {
        // проходим по всем деревьям из trees
        Iterator<Tree> treeIterator = trees.values().iterator();
        while (treeIterator.hasNext()){
            Tree tree = treeIterator.next();
            // находим ребро с минимальным весом minEdge, исходящее из дерева firstTreeToBeMerged
            Edge<N, E> minEdge = null;
            Tree firstTreeToBeMerged = null;
            Tree secondTreeToBeMerged = null;
            Node<N, E> firstTreeRoot = tree.getMasterNode(); // корневой элемент дерева
            // проходим по всем узлам дерева
            for (Node<N, E> node : tree.nodes) {
                for (Edge<N, E> edge : node.in) {
                    if (minEdge == null || minEdge.weight > edge.weight) {
                        Node<N, E> secondNode = edge.getSecond(node);
                        Node<N, E> secondTreeRoot = masterNodes.get(secondNode.info);
                        if (secondTreeRoot != firstTreeRoot) {
                            minEdge = edge;
                            firstTreeToBeMerged = tree;
                            secondTreeToBeMerged = trees.get(secondTreeRoot.info);
                        }
                    }
                }
            }
            // Второе дерево сливаем с первым, после чего второе дерево удаляем из Map<N, Tree> trees
            if (minEdge != null) {
                Node<N, E> secondTreeRoot = secondTreeToBeMerged.getMasterNode();
                for (Node<N, E> node : firstTreeToBeMerged.nodes) {
                    masterNodes.put(node.info, secondTreeRoot);
                }
                secondTreeToBeMerged.merge(firstTreeToBeMerged, minEdge);
                treeIterator.remove();
            }
        }
    }

    private List<Edge<N, E>> getMinTree(Graph<N, E> graph) {
        // masterNodes: ключ - node.info , значение - корневой узел(masterNode) дерева, которому принадлежит node
        Map<N, Node<N, E>> masterNodes = new HashMap<>();
        // trees: ключ - masterNode.info , значение - дерево, у которово корнем является masterNode
        Map<N, Tree> trees = new HashMap<>();
        initialize(masterNodes, trees, graph);
        callBoruvkaAlgorithm(masterNodes, trees, graph);
        if (trees.size() > 1) {
            throw new RuntimeException("Only one tree should remains");
        } else {
            for (Tree tree : trees.values()) {
                return tree.edges;
            }
        }
        return null;
    }

    private class Tree {
        Node<N, E> masterNode;
        List<Node<N, E>> nodes = new ArrayList<>();
        List<Edge<N, E>> edges = new ArrayList<>();

        Node<N, E> getMasterNode() {
            return masterNode;
        }

        void setMasterNode(Node<N, E> masterNode) {
            this.masterNode = masterNode;
        }

        void merge(Tree tree, Edge<N, E> connectingEdge) {
            nodes.addAll(tree.nodes);
            edges.add(connectingEdge);
            edges.addAll(tree.edges);
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
        edgeAB.in = nodeA;
        edgeAB.out = nodeB;
        Edge<String, String> edgeBC = new Edge<>();
        edgeBC.info = "BC";
        edgeBC.weight = 8;
        edgeBC.in = nodeB;
        edgeBC.out = nodeC;
        Edge<String, String> edgeAD = new Edge<>();
        edgeAD.info = "AD";
        edgeAD.weight = 5;
        edgeAD.in = nodeA;
        edgeAD.out = nodeD;
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
        edgeCE.in = nodeC;
        edgeCE.out = nodeE;
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
        edgeFE.in = nodeF;
        edgeFE.out = nodeE;
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

        nodeA.in.add(edgeAD);
        nodeA.in.add(edgeAB);
        nodeB.in.add(edgeAB);
        nodeB.in.add(edgeBC);
        nodeB.in.add(edgeBE);
        nodeB.in.add(edgeBD);
        nodeC.in.add(edgeBC);
        nodeC.in.add(edgeCE);
        nodeD.in.add(edgeAD);
        nodeD.in.add(edgeBD);
        nodeD.in.add(edgeDE);
        nodeD.in.add(edgeDF);
        nodeE.in.add(edgeCE);
        nodeE.in.add(edgeBE);
        nodeE.in.add(edgeDE);
        nodeE.in.add(edgeFE);
        nodeE.in.add(edgeEG);
        nodeF.in.add(edgeDF);
        nodeF.in.add(edgeFE);
        nodeF.in.add(edgeFG);
        nodeG.in.add(edgeFG);
        nodeG.in.add(edgeEG);
        Graph<String, String> graph = new Graph<>();
        graph.nodes.addAll(Set.of(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG));
        graph.edges.addAll(Set.of(edgeAB, edgeBC, edgeAD, edgeBD, edgeBE, edgeCE, edgeDE, edgeDF, edgeFE, edgeFG, edgeEG));

        BoruvkaV2.getSpanTree(graph).forEach(System.out::println);
    }
}
