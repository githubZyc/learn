package 反转链表;

import java.util.Stack;

/**
 * 功能描述:
 * 给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。
 * 输入：head = [1,2,3,4,5]
 * 输出：[5,4,3,2,1]
 *
 * 输入：head = [1,2]
 * 输出：[2,1]
 *
 * 输入：head = []
 * 输出：[]
 *
 * 提示：
 *
 * 链表中节点的数目范围是 [0, 5000]
 * -5000 <= Node.val <= 5000
 *  
 *
 * 进阶：链表可以选用迭代或递归方式完成反转。你能否用两种方法解决这道题？
 *
 * @Class Solution
 * @Author ZYC
 * @Date 2021/8/13 15:10
 * @Version 1.0
 **/
public class Solution {

    class ListNode{
        ListNode next;
        int val = 0;

        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    Stack<ListNode> stack = new Stack<>();

    public ListNode reverseList3(ListNode head){
        while (head!=null){
            stack.push(head);
            ListNode next = head.next;
            head = next;
        }
        ListNode pop = stack.pop();
        while (!stack.isEmpty()){

        }
        return head;
    }

    public ListNode reverseList2(ListNode head){
        ListNode r = null;
        while (head!=null){
            ListNode next = head.next;
            head.next = r;
            r = head;
            head=next;
        }
        return r;
    }

    // 1->2->3->4->5->null
    // 1 2 3 4 5 1
    public ListNode reverseList(ListNode head) {
        ListNode pre = null;
        ListNode curr = head;
        while(curr!=null){
            System.out.println(curr.val);
            //记录下次变量的链表 2->3->4->5->null 3->4->5->null 4->5->null
            ListNode temp = curr.next;
            // curr.next = 2->3->4->5->null : null  curr = 1->null curr.next = 3->4->5->null: curr.next=1->null curr=2->1->null curr.next = 2->1->null curr = 3->2->1->null
            curr.next = pre;
            // pre = 1->null pre = 2->1->null; pre = 3->2->1->null
            pre = curr;
            // 继续执行后续的链表 2->3->4->5->null 3->4->5->null
            curr = temp;
        }
        return pre;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        ListNode node = solution.createNode();
        ListNode result = solution.reverseList3(node);
        while (result!=null){
            System.out.println(result.val);
            result = result.next;
        }


    }

    public ListNode createNode() {
        ListNode listNode1 = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        listNode1.next = listNode2;
        ListNode listNode3 = new ListNode(3);
        listNode2.next = listNode3;
        ListNode listNode4 = new ListNode(4);
        listNode3.next = listNode4;
        ListNode listNode5 = new ListNode(5);
        listNode4.next = listNode5;
        return listNode1;
    }
}
