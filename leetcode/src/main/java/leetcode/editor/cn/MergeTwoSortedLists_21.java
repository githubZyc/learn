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


        ListNode listNode_3 = new ListNode(6,null);
        ListNode listNode_2 = new ListNode(5,listNode_3);
        ListNode listNode_1 = new ListNode(4,listNode_2);

        ListNode listNode = solution.mergeTwoLists(listNode1, listNode_1);

        while (listNode!=null){
            int val = listNode.val;
            System.out.println(val);
            listNode = listNode.next;
        }
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
            //创建存储拼接的对象
            ListNode result = new ListNode(-1);
            //创建指针
            ListNode pre = result;
            while (list1!=null && list2!=null){
                //循环取值
                if(list1.val<=list2.val){
                    pre.next = list1;
                    list1 = list1.next;
                }else{
                    pre.next = list2;
                    list2 = list2.next;
                }
                //改变指针指向
                pre = pre.next;
            }
            pre.next = (list1 == null ? list2 : list1);
            return result.next;
        }
    }
}