package GraphUtil;

public class Edge {
    public int from;
    public int adjvex;//边指向的点
    public int value;//边权值
    public int keyLeft;//密钥剩余量
    public Edge nextEdge;

    private int upperRange = 1000;
    public Edge() {}
    public Edge(int adjvex,int value)   //初始化边结点
    {
        this.adjvex=adjvex;
        this.value=value;
        this.nextEdge=null;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getValue() {
        return value;
    }

    synchronized public void setValue(int weight) {

        this.value = weight;
    }

    public int getUpperRange() {
        return upperRange;
    }

    public int getKeyLeft() {
        return keyLeft;
    }

    public void setKeyLeft(int keyLeft) {
        if (keyLeft > upperRange) {
            keyLeft= upperRange;
        }
        this.keyLeft = keyLeft;
    }

    public void setUpperRange(int upperRange) {
        this.upperRange = upperRange;
    }
}
