//将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
//
// 
//
// 示例 1： 
//
// 
//输入：l1 = [1,2,4], l2 = [1,3,4]
//输出：[1,1,2,3,4,4]
// 
//
// 示例 2： 
//
// 
//输入：l1 = [], l2 = []
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：l1 = [], l2 = [0]
//输出：[0]
// 
//
// 
//
// 提示： 
//
// 
// 两个链表的节点数目范围是 [0, 50] 
// -100 <= Node.val <= 100 
// l1 和 l2 均按 非递减顺序 排列 
// 
// Related Topics 递归 链表 
// 👍 2252 👎 0

package leetcode.editor.cn;

public class MergeTwoSortedLists_21 {
    public static void main(String[] args) {
        Solution solution = new MergeTwoSortedLists_21().new Solution();
        ListNode listNode3 = new ListNode(3,null);
        ListNode listNode2 = new ListNode(2,listNode3);
        ListNode listNode1 = new ListNode(1,listNode2);


        ListNode listNode_3 = new ListNode(3,null);
        ListNode listNode_2 = new ListNode(2,listNode_3);
        ListNode listNode_1 = new ListNode(1,listNode_2);

        solution.mergeTwoLists(listNode1,listNode_1);
        System.out.println("Hello world");
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    class Solution {
        public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
            ListNode result = list1;
            //当前不是null 继续遍历
            while (list1!=null){
                //获取链表中的第一个值
                int val = list1.val;
                System.out.println(val);
                //下移一位
                list1 = list1.next;
            }
            return null;
        }
    }
}