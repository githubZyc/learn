package 面试题.集合转树形结构;

import java.util.*;

public class Test {
    static class Node {
        private int id;
        private int parentId;
        private String name;

        public Node() {

        }

        public Node(int id, int parentId, String name) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        Test tree = new Test();
        List<Node> nodeList = Arrays.asList(
                new Node(1, 0, "AA"),
                new Node(2, 1, "BB"),
                new Node(3, 1, "CC"),
                new Node(4, 3, "DD"),
                new Node(5, 3, "EE"),
                new Node(6, 2, "FF"),
                new Node(7, 2, "GG"),
                new Node(8, 4, "HH"),
                new Node(9, 5, "II"),
                new Node(10, 0, "JJ"));
        Map<Integer, List<Node>> integerListMap = tree.convertList2(nodeList);
        //从根开始
        List<Node> nodeList1 = integerListMap.get(0);
        print(integerListMap,nodeList1,0);
    }

    /**
     * 功能描述: 数据结构化
     * @Author ZYC
     * @Date 2022/3/3 19:17
     * @Param [nodeList]
     * @Return java.util.Map<java.lang.Integer,java.util.List<com.atguigu.commonutils.tool.Test.Node>>
     * @Version 1.0
     **/
    private Map<Integer,List<Node>> convertList2(List<Node> nodeList) {
        //记录数据结构 0:[{n1,n2}],1:[{n3,n4}]...
        Map<Integer,List<Node>>parentChildrenListMap= new HashMap<>();
        //转成map结构
        for (Node node:nodeList){
            //寻找相同父级id的元素
            int parentId = node.getParentId();
            if(parentChildrenListMap.containsKey(parentId)){
                parentChildrenListMap.get(parentId).add(node);
            }else{
                List<Node> childNode = new ArrayList<>();
                childNode.add(node);
                parentChildrenListMap.put(parentId,childNode);
            }
        }
        return parentChildrenListMap;
    }

    /**
     * 功能描述: 当前节点下的子节点
     * @Author ZYC
     * @Date 2022/3/3 19:40
     * @Param [map, nodeList1]
     * @Return void
     * @Version 1.0
     **/
    private static void print(Map<Integer, List<Node>> map, List<Node> nodeList1,int space){
        for (int i=0;i< nodeList1.size();i++) {
            //输出每一次根的名字
            Node node = nodeList1.get(i);
            System.out.println(getT(space) + node.getName());
            //当前节点下的子节点
            List<Node> subNodes = map.get(node.getId());
            if (subNodes != null && subNodes.size() > 0) {
                //获取当前子节点下对应的下一级
                print(map, subNodes,space+1);
            }
        }
    }
    /**
     * 功能描述: 父级id相同的 生成相同的 \t
     * @Author ZYC
     * @Date 2022/3/3 14:41
     * @Param [n]
     * @Return java.lang.String
     * @Version 1.0
     **/
    static String getT(int n) {
        StringBuilder t = new StringBuilder("");
        for(int i =0;i<n;i++){
            t.append("\t");
        }
        return t.toString();
    }
}