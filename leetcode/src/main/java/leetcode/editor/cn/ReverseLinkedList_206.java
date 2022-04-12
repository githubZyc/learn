//给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。
// 
// 
// 
//
// 示例 1： 
//
// 
//输入：head = [1,2,3,4,5]
//输出：[5,4,3,2,1]
// 
//
// 示例 2： 
//
// 
//输入：head = [1,2]
//输出：[2,1]
// 
//
// 示例 3： 
//
// 
//输入：head = []
//输出：[]
// 
//
// 
//
// 提示： 
//
// 
// 链表中节点的数目范围是 [0, 5000] 
// -5000 <= Node.val <= 5000 
// 
//
// 
//
// 进阶：链表可以选用迭代或递归方式完成反转。你能否用两种方法解决这道题？ 
// 
// 
// Related Topics 递归 链表 
// 👍 2433 👎 0

package leetcode.editor.cn;

import 删除链表中倒数第n个节点.Test;

public class ReverseLinkedList_206{
    public static void main(String[] args) {
        Solution solution = new ReverseLinkedList_206().new Solution();
        ListNode nodeTest1 = new ReverseLinkedList_206().new ListNode(1);
        ListNode nodeTest2 = new ReverseLinkedList_206().new ListNode(2);
        ListNode nodeTest3 = new ReverseLinkedList_206().new ListNode(3);
        nodeTest1.next =nodeTest2;
        nodeTest2.next = nodeTest3;
        solution.reverseList(nodeTest1);
    }
    //leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.*/
  public class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

class Solution {
    public ListNode reverseList(ListNode head) {
        //存储下一个值
        ListNode pre = null;
        //当前指针
        ListNode curr = head;
        while (curr!=null){
            //记录链表的下个节点
            ListNode next = curr.next;
            //2 3 null  = null  curr = 1 null
            //更新链表下一个节点
            curr.next = pre;
            //curr 1 null   -> pre  pre:1->null
            pre = curr;
            //curr - > 2  3 null
            curr = next;
        }
        return pre;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

} 