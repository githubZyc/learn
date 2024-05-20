package chatgpt;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/5/14 09:46
 */

public class LinkNode1Test {
    class ListNode {
        int val;
        ListNode next;

        public ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }


    ListNode head = null;

    public ListNode addHeadToNode(int val){
        ListNode linkNode1 = new ListNode(val);
        linkNode1.next = head;
        head = linkNode1;
        return head;
    }

    public static void main(String[] args) {
        LinkNode1Test linkNode1Test = new LinkNode1Test();
        linkNode1Test.addHeadToNode(1);
        linkNode1Test.addHeadToNode(2);
    }
}
