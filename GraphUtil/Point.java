package GraphUtil;


public class Point{   //这个类用于邻接表，因为每一个顶点在邻接表中都存在一个指向其它顶点的指针域所以要将指针域和数据域封装成一个具体的类
    public int data;//该点id
    public Edge firstArc;//该点第一条边
    public Point() {}
    public Point(int data)
    {
        this.data=data;
        this.firstArc=null;
    }

}
