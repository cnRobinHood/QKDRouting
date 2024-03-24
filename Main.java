import GraphUtil.*;
import QKDUtil.QkdAlgorithm;

import java.util.List;

import static GraphUtil.Graph.initGraph;
import static QKDUtil.KeyUtil.generateKey;
import static QKDUtil.KeyUtil.lookAtLink;
import static QKDUtil.QkdAlgorithm.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = initGraph();
        updatePaths(graph);
        generateKey(graph);
        //QkdAlgorithm.RandomPathAlgorithm(graph,10);
        //QkdAlgorithm.DisjointPathAlgorithm(graph,10, 4);
        //QkdAlgorithm.MultiPathAlgorithm(graph,10, 4);
        //ApplicationDemandAlgorithm(graph,10,0,4);
//        initConcurrentAlgorithm(graph);
//        ConcurrentAlgorithm(graph,50,0,5);
//        ConcurrentAlgorithm(graph,50,1,5);
        lookAtLink(graph);
    }

    //使用路径代价优化算法之前，必须先init算法
    public static void initConcurrentAlgorithm(Graph graph) {
        for (int i = 0; i < graph.numPoint; i++) {
            Edge a = graph.point[i].firstArc;
            while (a != null) {
                EdgesUsedCount.put(new EdgeHelper(i,a.adjvex), 0);
                a = a.nextEdge;
            }
        }
    }
}