import java.util.Objects;

/**
 * 功能描述:
 *
 * @Class ListNodeTest
 * @Author ZYC
 * @Date 2021/5/13 11:16
 * @Version 1.0
 **/
public class ListNodeTest {
    //头节点
    private ListNode head = null;

    /**
     * 链表是由当前节点值 和下个指针组成
     */
    static class ListNode{
        int value ;
        ListNode nextNode;

        ListNode(int value){
            this.value = value;
        }
    }

    /**
     * 功能描述: 添加新节点
     * @Author ZYC
     * @Date 2021/5/13 14:01
     * @Param [a]
     * @Return boolean
     * @Version 1.0
     **/
    boolean addNode(int a){
        ListNode newNode = new ListNode(a);
        //记录头节点
        ListNode cur = head;
        if(cur == null){
          //头节点为null直接赋值头
            cur = newNode;
        }
        //一直遍历到尾部
        while (!Objects.isNull(cur.nextNode)){
            cur = cur.nextNode;
        }
        //赋值节点
        cur.nextNode = newNode;
        return true;
    }

    /**
     * 功能描述: 根据节点值删除当前节点
     * @Author ZYC
     * @Date 2021/5/13 14:44
     * @Param [a]
     * @Return boolean
     * @Version 1.0
     **/
    boolean deleteNode(int a){
        //头节点为null 不可删除
        if(this.head == null){
            return false;
        }
        //删除的值为头结点，将下一个值提升为头节点
        if(this.head.value == a){
            this.head = head.nextNode;
            return true;
        }
        ListNode cur = this.head;
        //循环节点
        while (!Objects.isNull(cur.nextNode)){
            //命中时直接指向下个节点
            if(cur.nextNode.value == a){
                cur.nextNode = cur.nextNode.nextNode;
                return true;
            }
            //继续寻找
            cur = cur.nextNode;
        }
        return false;
    }

    Integer getSize(){
        int size = 0;
        if(head.nextNode == null){
            return size;
        }
        ListNode cur = head;

        while (!Objects.isNull(cur.nextNode)){
            size ++;
            cur = cur.nextNode;
        }
        return size;
    }

    ListNode find(int a){
        if(head.value == a){
            return head;
        }
        ListNode cur = head;
        while (!Objects.isNull(cur.nextNode)){
            if(cur.nextNode.value == a){
                return cur.nextNode;
            }
            cur = cur.nextNode;
        }
        return null;
    }

    //revers
    public ListNode reverse(ListNode head){
        //传入头节点，翻转链表
        if(this.head  == head){
            return head;
        }
        //当前头部节点
        ListNode cur = head;
        ListNode prev = null;
        while (!Objects.isNull(cur.nextNode)){
            ListNode tempNode = cur.nextNode;
            cur.nextNode = prev;
            prev = cur;
            cur = tempNode;
        }
        return prev;
    }

    public static void main(String[] args) {
        //创建初始化一个链表结构，value = 0;nextNode = null;
        ListNode head=new ListNode(0);
        System.out.println("head.value:" + head.value + " head.nextNode:"+head.nextNode);

        ListNode first=new ListNode(1);
        ListNode secondNode = new ListNode(2);
        ListNode thirdNode = new ListNode(3);

        head.nextNode = first;
        first.nextNode = secondNode;
        secondNode.nextNode = thirdNode;
        thirdNode.nextNode = null;
    }

    private static void iteratorNode(ListNode node) {
        if(!Objects.isNull(node.value)){
            System.out.println(node.value);
            if(!Objects.isNull(node.nextNode)){
                iteratorNode(node.nextNode);
            }
        }
    }
}
