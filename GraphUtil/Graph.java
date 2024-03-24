package GraphUtil;

import QKDUtil.KeyUtil;

import java.util.List;

//图邻接表的表示法
public class Graph{
    public Point[] point;
    public int[] visted;
    public int numPoint;
    public int numEdeges;
    public Graph() {}
    public Graph(int numPoint,int numEdeges)
    {
        this.numPoint=numPoint;
        this.numEdeges=numEdeges;
        point=new Point[numPoint];  //初始化点集数组
        visted=new int[numPoint];
    }
    public void createMyGraph(Graph MyGraph,int numPoint,int numEdeges,int EdegesPoint[][])  //创建图
    {
        for(int i=0;i<numPoint;i++)
        {
            MyGraph.visted[i]=0;
            MyGraph.point[i]=new Point(i);   //录入顶点的数据域
        }
        for(int i=0;i<numEdeges;i++)   //初始化边表,这里使用到了链表中间的头插法
        {
            Edge a=new Edge(EdegesPoint[i][1],EdegesPoint[i][2]);         //记录出度
            a.nextEdge=MyGraph.point[EdegesPoint[i][0]].firstArc;  //头插法
            MyGraph.point[EdegesPoint[i][0]].firstArc=a;
        }
    }

    public void DFS(Graph graph,int m)
    {
        Edge a = null;
        graph.visted[m]=1;
        a=graph.point[m].firstArc;
        while(a!=null)
        {
            if(graph.visted[a.adjvex]==0)
                DFS(graph,a.adjvex);
            a=a.nextEdge;
        }
    }

    public void updateValueByKey()
    {
        List<Edge> edges = GraphUtil.getAllEdges(this);
        for (Edge edge : edges) {
            int upperRange = edge.getUpperRange();
            int keyLeft = edge.getValue();
            int keyGenSpeed = KeyUtil.getSpeed();
            edge.setValue((upperRange-keyLeft)/keyGenSpeed);
            edge.setKeyLeft(keyLeft);
        }
    }

    public static Graph initGraph() {
//        int n=6;
//        int e=9;
//        //9行三列数组，每一列分别为开始点、终止点、该边权值
//        int data[][]={{0,1,200},{0,2,400},{1,5,400},{1,4,200},{2,3,100},{2,5,400},{4,3,100},{4,5,200},{3,5,100}};

        int n=6;
        int e=6;
        //9行三列数组，每一列分别为开始点、终止点、该边权值
        int data[][]={{1,3,900},{0,2,800},{4,5,900},{3,5,900},{2,3,900},{2,4,800}};

        Graph graph=new Graph(n,e);
        graph.createMyGraph(graph,n,e,data);
        graph.DFS(graph,0);//从0开始遍历
        graph.updateValueByKey();
        initEdgesFrom(graph);
        return graph;
    }
    public static void initEdgesFrom(Graph graph) {
        for (int i = 0; i < graph.numPoint; i++) {
            Edge a=graph.point[i].firstArc;
            while(a!=null)
            {
                a.setFrom(i);
                a = a.nextEdge;
            }
        }
    }

}

