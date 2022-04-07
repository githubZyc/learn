//给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。 
//
// 请你将两个数相加，并以相同形式返回一个表示和的链表。 
//
// 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。 
//
// 
//
// 示例 1： 
//
// 
//输入：l1 = [2,4,3], l2 = [5,6,4]
//输出：[7,0,8]
//解释：342 + 465 = 807.
// 
//
// 示例 2： 
//
// 
//输入：l1 = [0], l2 = [0]
//输出：[0]
// 
//
// 示例 3： 
//
// 
//输入：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
//输出：[8,9,9,9,0,0,0,1]
// 
//
// 
//
// 提示： 
//
// 
// 每个链表中的节点数在范围 [1, 100] 内 
// 0 <= 数据Node.val <= 9
//// 题目保证列表表示的数字不含前导零
// 
// Related Topics 递归 链表 数学 
// 👍 7845 👎 0

package leetcode.editor.cn;

public class AddTwoNumbers_2 {
    public static void main(String[] args) {
        Solution solution = new AddTwoNumbers_2().new Solution();
        ListNode t1 = new ListNode(2);
        ListNode t2 = new ListNode(4);
        t1.next = t2;
        ListNode t3 = new ListNode(3);
        t2.next = t3;
        ListNode k1 = new ListNode(5);
        ListNode k2 = new ListNode(6);
        ListNode k3 = new ListNode(4);
        k1.next = k2;
        k2.next = k3;
        ListNode listNode = solution.addTwoNumbers(t1, k1);
        while (listNode!=null){
            System.out.print(listNode.val);
            listNode = listNode.next;
        }
    }
    //leetcode submit region begin(Prohibit modification and deletion)

    /**
     * Definition for singly-linked list.
     */
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
        public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
            ListNode re = new ListNode(0);
            ListNode cur = re;
            int carry = 0;
            while (l1!=null || l2!=null){
                int a = 0,b=0;
                if(l1 != null){
                    a = l1.val;
                    l1 = l1.next;
                }
                if(l2 != null){
                    b = l2.val;
                    l2 = l2.next;
                }
                int temp = a + b + carry;
                if(temp<10){
                    carry = 0;
                }else {
                    carry = 1;
                }
                int sum = temp%10;
                //当前链表的下个值
                cur.next = new ListNode(sum);
                //当前节点赋值
                cur = cur.next;
            }
            if(carry>0){
                //当前链表的下个值
                cur.next = new ListNode(carry);
            }
            return re.next;
        }
    }
//leetcode submit region end(Prohibit modification and deletion)

} 