package 删除链表中倒数第n个节点;

import leetcode.editor.cn.AddTwoNumbers_2;

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
        NodeTest nodeTest1 = new NodeTest(1);
        NodeTest nodeTest2 = new NodeTest(2);
        NodeTest nodeTest3 = new NodeTest(3);
        nodeTest1.next =nodeTest2;
        nodeTest2.next = nodeTest3;

        NodeTest nodeTest4 = new NodeTest(9);
        NodeTest nodeTest5 = new NodeTest(9);
        NodeTest nodeTest6 = new NodeTest(9);
        nodeTest4.next =nodeTest5;
        nodeTest5.next = nodeTest6;

        NodeTest nodeTest = addNode(nodeTest1, nodeTest4);
        nodeTest = reverseList(nodeTest1);
        while (nodeTest!=null){
            System.out.println(nodeTest.value);
            nodeTest = nodeTest.next;
        }

    }

    /**
     * 功能描述: 链表翻转
     * @Author ZYC
     * @Date 2022/4/12 14:44
     * @Param [nodeTest1, nodeTest4]
     * @Return 删除链表中倒数第n个节点.Test.NodeTest
     * @Version 1.0
     **/
    private static NodeTest reverseList(NodeTest nodeTest1) {
        NodeTest pre = null; //前一个节点
        NodeTest curr = nodeTest1; //当前节点
        while (curr!=null){
            NodeTest temp = curr.next;
            curr.next = pre;
            pre = curr;
            curr = temp;
        }
       return pre;
    }

    private static NodeTest addNode(NodeTest nodeTest1, NodeTest nodeTest2) {
        NodeTest result = new NodeTest(0);
        NodeTest tmp = result;

        int shi = 0;
        while (nodeTest1!=null || nodeTest2!=null){
            int sum = (nodeTest1 != null ? nodeTest1.value : 0) + (nodeTest2 != null ? nodeTest2.value : 0) + shi;
            if(sum>=10){
                shi = 1;
            }else{
                shi = 0;
            }
            tmp.next = new NodeTest(sum%10);
            tmp = tmp.next;
            nodeTest1 = nodeTest1.next;
            nodeTest2 = nodeTest2.next;
        }
        if(shi>0){
            tmp.next = new NodeTest(1);
        }
        return result.next;
    }
}
