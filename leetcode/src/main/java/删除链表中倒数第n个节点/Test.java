package 删除链表中倒数第n个节点;

import java.util.Objects;

/**
 * 功能描述:
 * 要删除倒数第n个节点，我们就要找到其前面一个节点，也就是倒数第n+1个节点，
 * 找到这个节点就可以进行删除
 * @Class Test
 * @Author ZYC
 * @Date 2021/5/18 18:58
 * @Version 1.0
 **/
public class Test {
    static class NodeTest{
        int value;
        NodeTest next;

        NodeTest(int value){
            this.value = value;
        }

        public static NodeTest delLastN(NodeTest head,int n){
            //定义两个链表指针，第二个先走N步
            NodeTest cur = head,prex = head;
            for(int i =0;i<n;i++){
                prex = prex.next;
            }
            // prex指向了第N步的位置
            while (!Objects.isNull(prex.next)){
                prex = prex.next;
                cur = cur.next;
            }
            cur.next=cur.next.next;
            return head;
        }
    }
    public static void main(String[] args) {
        NodeTest nodeTest = new NodeTest(0);
        NodeTest nodeTest1 = new NodeTest(1);
        NodeTest nodeTest2 = new NodeTest(2);
        NodeTest nodeTest3 = new NodeTest(3);
        NodeTest nodeTest4 = new NodeTest(4);
        NodeTest nodeTest5 = new NodeTest(5);
        nodeTest.next = nodeTest1;
        nodeTest1.next = nodeTest2;
        nodeTest2.next = nodeTest3;
        nodeTest3.next = nodeTest4;
        nodeTest4.next = nodeTest5;
        nodeTest5.next = null;

//        while (!Objects.isNull(nodeTest.next)){
//            if(!Objects.isNull(nodeTest.next.value)){
//                System.out.println(nodeTest.next.value);
//                nodeTest = nodeTest.next;
//            }
//        }

        NodeTest nodeTest6 = NodeTest.delLastN(nodeTest, 2);
        while (!Objects.isNull(nodeTest6.next)){
            if(!Objects.isNull(nodeTest6.next.value)){
                System.out.println(nodeTest6.next.value);
                nodeTest6 = nodeTest6.next;
            }
        }
    }
}
