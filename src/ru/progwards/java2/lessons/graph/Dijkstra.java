package ru.progwards.java2.lessons.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Dijkstra {
    int[][] graph;

    Dijkstra( int[][] graph){
        this.graph = graph;
    }

    public int[][] find(int n){
        // initialization
        BinaryHeap<Score> scores = new BinaryHeap<>(BinaryHeap.Type.MIN_HEAP);
        HashMap<Integer, ArrayList<Edge>> edges = new HashMap<>();
        ArrayList[] paths = new ArrayList[graph.length];
        for (int nodeNum=0; nodeNum<graph.length; nodeNum++){
            if (nodeNum == n)
                scores.add(new Score(nodeNum, 0));
            else
                scores.add(new Score(nodeNum, Integer.MAX_VALUE));
            ArrayList<Edge> edgesFromNode = new ArrayList<>();
            for (int j=0; j<graph[nodeNum].length; j++){
                if (graph[nodeNum][j] > 0)
                    edgesFromNode.add(new Edge(nodeNum, j, graph[nodeNum][j]));
            }
            edges.put(nodeNum, edgesFromNode);
            ArrayList<Integer> path = new ArrayList<>();
            path.add(n);
            paths[nodeNum] = path;
        }
        // algorithm
        while (scores.size() > 0){
            Score scoreOfStartNode = scores.poll(); // node with minimum value of weight
            int startNode = scoreOfStartNode.node;
            for (Edge edge : edges.get(startNode)){
                int endNode = edge.second(startNode);
                Score scoreOfEndNode = null;
                int scoreIndexOfEndNode = 0;
                for (Score score : scores){
                    if (score.node == endNode){
                        scoreOfEndNode = score;
                        break;
                    }
                    scoreIndexOfEndNode++;
                }
                if (scoreOfEndNode != null){
                    if (scoreOfStartNode.weight+edge.weight < scoreOfEndNode.weight){
                        scoreOfEndNode.weight = scoreOfStartNode.weight+edge.weight;
                        scores.shiftUp(scoreIndexOfEndNode);
                        // меняем путь для endNode
                        paths[endNode].clear();
                        paths[endNode].addAll(paths[startNode]);
                        paths[endNode].add(endNode);
                    }
                }
            }
        }
        // cast result to int[][]
        int[][] resultPaths = new int[paths.length][];
        for (int i=0; i<paths.length; i++){
            ArrayList path = paths[i];
            Object[] pathArr = path.toArray();
            int[] resPath = new int[pathArr.length];
            for (int j=0; j<pathArr.length; j++){
                resPath[j] = (int)pathArr[j];
            }
            resultPaths[i] = resPath;
        }
        return resultPaths;
    }

    private class Score implements Comparable<Score> {
        int node;
        int weight;

        Score(int node, int weight){
            this.node = node;
            this.weight = weight;
        }

        @Override
        public int compareTo(Score o) {
            return Integer.compare(this.weight, o.weight);
        }

        @Override
        public String toString() {
            return "{node=" + node + ", score=" + weight + '}';
        }
    }

    private class Edge {
        int a;
        int b;
        int weight;

        Edge(int a, int b, int weight){
            this.a = a;
            this.b = b;
            this.weight = weight;
        }

        int second(int first){
            if (first == a)
                return b;
            if (first == b)
                return a;
            throw new NoSuchElementException("node is not incident this edge");
        }
    }

    public static void main(String[] args) {
        int[][] graph = {{0,7,9,0,0,14},{7,0,10,15,0,0},{9,10,0,11,0,2},{0,15,11,0,6,0},{0,0,0,6,0,9},{14,0,2,0,9,0}};
        Dijkstra dijkstra = new Dijkstra(graph);
        int[][] shortestPaths = dijkstra.find(0);
        System.out.println(Arrays.deepToString(shortestPaths));
    }
}
