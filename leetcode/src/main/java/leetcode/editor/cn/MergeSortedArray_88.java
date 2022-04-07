//给你两个按 非递减顺序 排列的整数数组 nums1 和 nums2，另有两个整数 m 和 n ，分别表示 nums1 和 nums2 中的元素数目。 
//
// 请你 合并 nums2 到 nums1 中，使合并后的数组同样按 非递减顺序 排列。 
//
// 注意：最终，合并后数组不应由函数返回，而是存储在数组 nums1 中。为了应对这种情况，nums1 的初始长度为 m + n，其中前 m 个元素表示应合并
//的元素，后 n 个元素为 0 ，应忽略。nums2 的长度为 n 。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
//输出：[1,2,2,3,5,6]
//解释：需要合并 [1,2,3] 和 [2,5,6] 。
//合并结果是 [1,2,2,3,5,6] ，其中斜体加粗标注的为 nums1 中的元素。
// 
//
// 示例 2： 
//
// 
//输入：nums1 = [1], m = 1, nums2 = [], n = 0
//输出：[1]
//解释：需要合并 [1] 和 [] 。
//合并结果是 [1] 。
// 
//
// 示例 3： 
//
// 
//输入：nums1 = [0], m = 0, nums2 = [1], n = 1
//输出：[1]
//解释：需要合并的数组是 [] 和 [1] 。
//合并结果是 [1] 。
//注意，因为 m = 0 ，所以 nums1 中没有元素。nums1 中仅存的 0 仅仅是为了确保合并结果可以顺利存放到 nums1 中。
// 
//
// 
//
// 提示： 
//
// 
// nums1.length == m + n 
// nums2.length == n 
// 0 <= m, n <= 200 
// 1 <= m + n <= 200 
// -109 <= nums1[i], nums2[j] <= 109 
// 
//
// 
//
// 进阶：你可以设计实现一个时间复杂度为 O(m + n) 的算法解决此问题吗？ 
// Related Topics 数组 双指针 排序 
// 👍 1373 👎 0

package leetcode.editor.cn;

import java.util.Arrays;

public class MergeSortedArray_88{
    public static void main(String[] args) {
        Solution solution = new MergeSortedArray_88().new Solution();
        int[] t1 = new int[]{5,7,9,0,0,0};
        int[] t2 = new int[]{1,2,3};
        solution.merge(t1,3,t2,3);
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public void merge(int[] t1, int m, int[] t2, int n) {
        //容器
        int[] res = new int[m + n];
        //a1 为t1的下标，a2 = t2的下标，k= 新数组
        int a1=0,a2=0,k =0;
        //结束条件为当一个数组遍历完就跳出循环
        while (a1<m && a2<n){
            if(t1[a1]<=t2[a2]){
                res[k] = t1[a1];
                a1++;
            }else{
                res[k] = t2[a2];
                a2++;
            }
            k++;
        }
        while (a1<m){
            res[n+a1] = t1[a1++];
        }
        while (a2<n){
            res[m + a2] = t2[a2++];
        }
        // 将tmp中的数字排到nums1里
        for (int i = 0; i < t1.length; i++) {
            t1[i] = res[i];
        }
        System.out.println(Arrays.toString(t1));
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}