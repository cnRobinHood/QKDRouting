package QKDUtil;

import GraphUtil.Edge;
import GraphUtil.Graph;
import GraphUtil.GraphUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyUtil {
    static int speed = 10;

    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        KeyUtil.speed = speed;
    }

    public static void generateKey(Graph graph) {

        Thread generateKeyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    generateKeys(graph, speed);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
        generateKeyThread.start();
    }

    public static void generateKeys(Graph graph, int generateSpeed) {
        if (graph == null) {
            return;
        }
        List<Edge> edges = GraphUtil.getAllEdges(graph);
        for (Edge edge : edges) {
            int originalKey = edge.getKeyLeft();
            edge.setKeyLeft(originalKey + generateSpeed);
        }

    }

    public static int consumeKeys(Graph graph, List<List<Integer>> paths, int consumeSpeed) {
        int sum = 0;
        for (int i = 0; i < paths.size(); i++) {
            for (int j = 0; j < paths.get(i).size(); j++) {
                if (j == paths.get(i).size() - 1) {
                    continue;
                }
                Edge edge = GraphUtil.getEdge(graph, paths.get(i).get(j), paths.get(i).get(j + 1));

                int keysLeft = edge.getKeyLeft();
                int result = keysLeft - consumeSpeed;
                if (result < 0) {
                    System.out.println
                            ("因为" + paths.get(i).get(j) + "到" + paths.get(i).get(j + 1) + "的密钥余量为" + edge.getKeyLeft() + ". 不足以支撑业务");
                    System.out.println("虽然失败了，但是消耗了" + sum);
                    return -1;
                } else {
                    sum += consumeSpeed;
                }
                edge.setKeyLeft(result);
            }
        }
        return sum;
    }


    public static void lookAtLink(Graph graph) {
        Thread lookAtLinkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println();
                List<Integer> integers = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                while (true) {
                    int[] list = new int[9];
                    System.out.println("当前时间：" + (System.currentTimeMillis() - startTime));
                    System.out.println("查看链路密钥情况：");
                    int sum = 0;

                    Edge a = null;
                    List<Edge> edges = new ArrayList<>();
                    for (int i = 0; i < graph.numPoint; i++) {
                        a = graph.point[i].firstArc;
                        while (a != null) {
                            edges.add(a);
                            if (i == 0 && a.adjvex == 1) {
                                list[0] = a.getKeyLeft() - 200;
                            }
                            if (i == 0 && a.adjvex == 2) {
                                list[1] = a.getKeyLeft() - 400;
                            }
                            if (i == 1 && a.adjvex == 5) {
                                list[2] = a.getKeyLeft() - 400;
                            }
                            if (i == 1 && a.adjvex == 4) {
                                list[3] = a.getKeyLeft() - 200;
                            }
                            if (i == 2 && a.adjvex == 3) {
                                list[4] = a.getKeyLeft() - 100;
                            }
                            if (i == 2 && a.adjvex == 5) {
                                list[5] = a.getKeyLeft() - 400;
                            }
                            if (i == 4 && a.adjvex == 3) {
                                list[6] = a.getKeyLeft() - 100;
                            }
                            if (i == 4 && a.adjvex == 5) {
                                list[7] = a.getKeyLeft() - 200;
                            }
                            if (i == 3 && a.adjvex == 5) {
                                list[8] = a.getKeyLeft() - 100;
                            }
                            System.out.println("from " + i + " to " + a.adjvex + ": " + a.getKeyLeft());
                            sum += a.getKeyLeft();
                            a = a.nextEdge;
                        }
                    }
                    System.out.println("此时链路密钥总量为:" + sum);
                    integers.add(sum);
                    System.out.println(Arrays.toString(list));
                    System.out.println(integers);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        lookAtLinkThread.start();
    }


}
