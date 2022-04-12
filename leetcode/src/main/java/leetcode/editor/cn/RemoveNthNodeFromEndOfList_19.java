//给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。 
//
// 
//
// 示例 1： 
//
// 
//输入：head = [1,2,3,4,5], n = 2
//输出：[1,2,3,5]
// 
//
// 示例 2： 
//
// 
//输入：head = [1], n = 1
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：head = [1,2], n = 1
//输出：[1]
// 
//
// 
//
// 提示： 
//
// 
// 链表中结点的数目为 sz 
// 1 <= sz <= 30 
// 0 <= Node.val <= 100 
// 1 <= n <= sz 
// 
//
// 
//
// 进阶：你能尝试使用一趟扫描实现吗？ 
// Related Topics 链表 双指针 
// 👍 1954 👎 0

package leetcode.editor.cn;


import java.util.Objects;

public class RemoveNthNodeFromEndOfList_19{
    public static void main(String[] args) {
        Solution solution = new RemoveNthNodeFromEndOfList_19().new Solution();
        ListNode nodeTest1 = new RemoveNthNodeFromEndOfList_19().new ListNode(1);
        ListNode nodeTest2 = new RemoveNthNodeFromEndOfList_19().new ListNode(2);
//        ListNode nodeTest3 = new RemoveNthNodeFromEndOfList_19().new ListNode(3);
//        ListNode nodeTest4 = new RemoveNthNodeFromEndOfList_19().new ListNode(4);
//        ListNode nodeTest5 = new RemoveNthNodeFromEndOfList_19().new ListNode(5);
        nodeTest1.next = nodeTest2;
//        nodeTest2.next = nodeTest3;
//        nodeTest3.next = nodeTest4;
//        nodeTest4.next = nodeTest5;
//        nodeTest5.next = null;
        ListNode nodeTest6 = solution.removeNthFromEnd(nodeTest1, 2);
        while (!Objects.isNull(nodeTest6)){
            System.out.println(nodeTest6.val);
            nodeTest6 = nodeTest6.next;
        }
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
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode lst = new ListNode(0,head);
        //创建双指针进行遍历
        ListNode curr = lst,pre = head;
        //pre 先走n步 剩下的链表长度正好为 curr指向到要删除的位置 例如   1 2 3 4 5  删除倒数 2个        pre = 3 4 5  curr 1 2 3 4 及当前curr.val = 4  curr =  curr.next curr 1 2 3 5
        for (int i = 0; i < n; i++) {
            //1 pre = 2 3 4 5
            //2 pre = 3 4 5
            pre = pre.next;
        }
        while (pre!=null){
            pre = pre.next;
            curr = curr.next;
        }
        curr.next = curr.next.next;
        return lst.next;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

} 