package GraphUtil;

import java.util.ArrayList;
import java.util.List;

public class GraphUtil {
    public static double getEdgeWight(Graph graph, int i, int j)
    {
        double w=0;
        Edge a = null;
        a=graph.point[i].firstArc;
        while(a!=null)
        {
            if(a.adjvex==j)
            {
                return w+a.value;
            }
            a=a.nextEdge;
        }
        return -1;
    }

    public static Edge getEdge(Graph graph, int i, int j)
    {
        double w=0;
        Edge a = null;
        a=graph.point[i].firstArc;
        while(a!=null)
        {
            if(a.adjvex==j)
            {
                return a;
            }
            a=a.nextEdge;
        }
        return null;
    }

    public static boolean isConnected(Graph graph,int i,int j)
    {
        double w=0;
        Edge a = null;
        a=graph.point[i].firstArc;
        while(a!=null)
        {
            if(a.adjvex==j)
            {
                return true;
            }
            a=a.nextEdge;
        }
        return false;
    }

    public static List<Edge> getAllEdges(Graph graph)
    {
        Edge a = null;
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < graph.numPoint; i++) {
            a=graph.point[i].firstArc;
            while(a!=null)
            {
                edges.add(a);
                a=a.nextEdge;
            }
        }
        return edges;
    }

}
