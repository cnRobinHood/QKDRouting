package QKDUtil;

import GraphUtil.*;

import java.util.*;

import static QKDUtil.KeyUtil.consumeKeys;
import static QKDUtil.KeyUtil.getSpeed;

public class QkdAlgorithm {
    public static List<List<Integer>> bestPaths = new ArrayList<>();

    public static void RandomPathAlgorithm(Graph graph, int consumeSpeed) {
        Thread useRandomPathThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //用于判断链路是否还能继续传输密钥
                boolean flag = true;
                List<List<Integer>> list = new ArrayList<>();
                //可以替换成量子随机数
                list.add(bestPaths.get(new Random().nextInt(bestPaths.size())));
                //list.add(bestPaths.get(0));
                while (flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int resultCode = consumeKeys(graph, list, consumeSpeed);
                    if (resultCode < 0) {
                        flag = false;
                        System.out.println("密钥不足，任务无法继续");

                    }
                    updatePaths(graph);
                    list.clear();
                    list.add(bestPaths.get(new Random().nextInt(bestPaths.size())));

                }

            }
        });
        useRandomPathThread.start();
    }

    public static void DisjointPathAlgorithm(Graph graph, int consumeSpeed, int n) {
        if (bestPaths.size() < n) {
            n = bestPaths.size();
        }
        List<List<List<Integer>>> disJointPaths = new ArrayList<>();
        getDisjointPaths(bestPaths.subList(0, n), 0, disJointPaths);
        for (List<List<Integer>> list : disJointPaths) {
            System.out.println(list);
        }
        int finalN = n;
        Thread useDisJointPathThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //用于判断链路是否还能继续传输密钥
                boolean flag = true;

                while (flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    //此处的Random可以替换为量子随机数
                    List<List<Integer>> list = disJointPaths.get(new Random().nextInt(disJointPaths.size()));
                    //System.out.println("没有公共节点的路径为："+list);
                    int resultCode = consumeKeys(graph, list, consumeSpeed);
                    if (resultCode < 0) {
                        flag = false;
                        System.out.println("密钥不足，任务无法继续");

                    }
                    updatePaths(graph);
                    disJointPaths.clear();
                    getDisjointPaths(bestPaths.subList(0, finalN), 0, disJointPaths);
                }
            }
        });
        useDisJointPathThread.start();
    }

    public static void MultiPathAlgorithm(Graph graph, int consumeSpeed, int n) {
        if (bestPaths.size() < n) {
            n = bestPaths.size();
        }
        int finalN = n;
        Thread useMultiPathThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //用于判断链路是否还能继续传输密钥
                boolean flag = true;
                while (flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int resultCode = consumeKeys(graph, bestPaths.subList(0, finalN), consumeSpeed);
                    if (resultCode < 0) {
                        flag = false;
                        System.out.println("密钥不足，任务无法继续");
                    }
                    updatePaths(graph);
                }
            }
        });
        useMultiPathThread.start();
    }


    public static void ApplicationDemandAlgorithm(Graph graph, int consumeSpeed, int level, int n) {
        int policy = jurgePolicy(consumeSpeed, level, getSpeed());
        System.out.println("policy is " + policy);
        switch (policy) {
            case 0:
                System.out.println("量子随机路径算法启动!");
                RandomPathAlgorithm(graph, consumeSpeed);
                break;
            case 1:
                System.out.println("堆优化多路径启动!");
                DisjointPathAlgorithm(graph, consumeSpeed, n);
                break;
            case 2:
                System.out.println("多路径算法启动!");
                MultiPathAlgorithm(graph, consumeSpeed, n);
                break;
        }
    }

    public static int retryCount = 0;

    public static int reComputeSleepTime(int retryCount) {
        System.out.println("准备重试第" + retryCount + "次");
        int unitTime = 10;
        return unitTime * (int) Math.pow(2, retryCount);
    }


    public static HashMap<EdgeHelper, Integer> EdgesUsedCount = new HashMap<>();

    public synchronized static void reduceEdgesUsedCount(List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            for (EdgeHelper helper : EdgesUsedCount.keySet()) {
                if (helper.getFrom() == path.get(i) && helper.getTo() == path.get(i + 1)) {
                    EdgesUsedCount.replace(helper, EdgesUsedCount.get(helper) - 1);

                }
            }
        }
    }

    public synchronized static void addEdgesUsedCount(List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            for (EdgeHelper helper : EdgesUsedCount.keySet()) {
                if (helper.getFrom() == path.get(i) && helper.getTo() == path.get(i + 1)) {
                    EdgesUsedCount.replace(helper, EdgesUsedCount.get(helper) + 1);
                }
            }
        }
    }

    //
    public static void ConcurrentAlgorithm(Graph graph, int consumeSpeed, int start, int end) {

        Thread useConcurrentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //用于判断链路是否还能继续传输密钥
                boolean flag = true;
                while (flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    List<Integer> path = updateConPaths(graph, start, end);
                    addEdgesUsedCount(path);
                    System.out.println(EdgesUsedCount);
                    System.out.println(path);
                    List<List<Integer>> tempPath = new ArrayList<>();
                    tempPath.add(path);
                    int resultCode = consumeKeys(graph, tempPath, consumeSpeed);
                    if (resultCode < 0) {
                        flag = false;
                        System.out.println("密钥不足，任务无法继续");
                    }
                    reduceEdgesUsedCount(path);

                    if (resultCode < 0) {
                        if (retryCount == 16) {
                            flag = false;
                            System.out.println("重试了16次，还是不行哦");
                            break;
                        }
                        retryCount += 1;
                        int sleepTime = reComputeSleepTime(retryCount);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        retryCount = 0;
                        System.out.println("此次消耗的密钥为:" + resultCode);
                    }
                }

            }
        });

        useConcurrentThread.start();
    }

    public static void getDisjointPaths(List<List<Integer>> path, int count, List<List<List<Integer>>> disJointPaths) {
        if (path.isEmpty() || count == path.size()) {
            return;
        }
        List<Integer> target = path.get(count);
        List<List<Integer>> tempPath = new ArrayList<>();
        tempPath.add(target);
        for (int i = count + 1; i < path.size(); i++) {
            boolean flag = true;
            for (int j = 1; j < path.get(i).size() - 1; j++) {
                if (target.contains(path.get(i).get(j))) {
                    flag = false;
                }
                for (List<Integer> list : tempPath) {
                    if (list.contains(path.get(i).get(j))) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                tempPath.add(path.get(i));
                disJointPaths.add(tempPath);
            }
        }
        count += 1;
        getDisjointPaths(path, count, disJointPaths);
    }

    //用于第五章判断策略
    public static int jurgePolicy(int k, int level, int speed) {
        if (level == 1 || k < speed * 0.5) {
            return 2;
        }
        if (k >= speed) {
            return 0;
        }
        if (k >= speed * 0.5) {
            return 1;
        }
        return -1;
    }

    public static List<Integer> updateConPaths(Graph graph, int start, int end) {
        Graph tempGraph = Graph.initGraph();
        for (int i = 0; i < graph.numPoint; i++) {
            Edge a = graph.point[i].firstArc;
            while (a != null) {
                Edge edge = GraphUtil.getEdge(tempGraph, i, a.adjvex);
                if (edge != null) {
                    edge.setKeyLeft(a.getKeyLeft());
                }
                a = a.nextEdge;
            }
        }
        ShortestPath ksp = new ShortestPath();
        updateValueByKeyAndAlpha(tempGraph, 10);
        List<ShortestPath.MyPath> myPaths = ksp.KSP_Yen(tempGraph, start, end, 8);
        System.out.println(myPaths);
        return myPaths.get(0).path;
    }

    public static void updatePaths(Graph graph) {
        bestPaths.clear();
        ShortestPath ksp = new ShortestPath();
        updateValueByKey(graph);
        List<ShortestPath.MyPath> myPaths = ksp.KSP_Yen(graph, 0, 5, 8);
        for (ShortestPath.MyPath myPath : myPaths) {
            bestPaths.add(myPath.path);
        }
        System.out.println(myPaths);
    }

    public static void updateValueByKey(Graph graph) {
        List<Edge> edges = GraphUtil.getAllEdges(graph);
        for (Edge edge : edges) {
            int upperRange = edge.getUpperRange();
            int keyLeft = edge.getKeyLeft();
            int keyGenSpeed = KeyUtil.getSpeed();
            edge.setValue((upperRange - keyLeft) / keyGenSpeed);
        }
    }

    public static void updateValueByKeyAndAlpha(Graph graph, int alpha) {
        for (int i = 0; i < graph.numPoint; i++) {
            Edge edge = graph.point[i].firstArc;
            while (edge != null) {
                int upperRange = edge.getUpperRange();
                int keyLeft = edge.getKeyLeft();
                int keyGenSpeed = KeyUtil.getSpeed();
                int value = (upperRange - keyLeft) / keyGenSpeed;
                for (EdgeHelper helper : EdgesUsedCount.keySet()) {
                    if (helper.getFrom() == i && helper.getTo() == edge.adjvex && EdgesUsedCount.get(helper) > 1) {
                        value += (alpha * (EdgesUsedCount.get(helper) - 1));
                        break;
                    }
                }
                edge.setValue(value);
                edge = edge.nextEdge;
            }
        }
    }

}
