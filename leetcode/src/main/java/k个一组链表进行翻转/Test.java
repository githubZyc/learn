package k个一组链表进行翻转;

import 反转链表.Solution;

import java.util.Objects;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/5/24 10:41
 * @Version 1.0
 **/
public class Test {
    public static class NodeTest{
        int value;
        NodeTest next ;
        public NodeTest(int value){
            this.value  = value;
            this.next = null;
        }
    }

    public static void main(String[] args) {
        NodeTest nodeTest = new NodeTest(0);
        NodeTest nodeTest1 = new NodeTest(1);
        NodeTest nodeTest2 = new NodeTest(2);
        NodeTest nodeTest3 = new NodeTest(3);
        NodeTest nodeTest4 = new NodeTest(4);
        NodeTest nodeTest5 = new NodeTest(5);
        NodeTest nodeTest6 = new NodeTest(6);

        nodeTest.next = nodeTest1;
        nodeTest1.next = nodeTest2;
        nodeTest2.next = nodeTest3;
        nodeTest3.next = nodeTest4;
        nodeTest4.next = nodeTest5;
        nodeTest5.next = nodeTest6;
        nodeTest6.next = null;
//        while (!Objects.isNull(nodeTest.next)){
//            nodeTest = nodeTest.next;
//            System.out.println(nodeTest.value);
//        }
        reverseList2(nodeTest);

    }

    /**
     * 功能描述: 按组翻转
     * @Author ZYC
     * @Date 2021/5/24 10:47
     * @Param [head, k]
     * @Return com.media.config.leetcode.k个一组链表进行翻转.Test.NodeTest
     * @Version 1.0
     **/
    public static NodeTest reverseKGroup(NodeTest node, int k) {
        NodeTest cur = node;
        int length = 0;
        do {
            length ++;
            node =  node.next;
        }while (!Objects.isNull(node.next));

        int group = length / k;
        print(cur,group,k);
        return null;
    }

    private static void print(NodeTest node,int group,int k) {
        int currK = k;
        NodeTest result = null;
        do {
           if(node!=null){
               NodeTest tempNode = null;NodeTest pre = null;
               if(currK>0){
                   int value = node.value;
                   System.out.println(value);
                   node = node.next;
                   currK--;
               }else{
                   //达到翻转条件
                   currK = k;
                   group --;
                   //翻转

               }
           }
        }while (group>0);
        System.out.println(node);
    }

    public static NodeTest reverseList2(NodeTest head) {
        NodeTest res = null;
        while (head != null) {
            //临时记录
            NodeTest temp = head.next;
            head.next = res;
            res = head;
            head = temp;
        }
        System.out.println(res);
        return res;
    }
}
