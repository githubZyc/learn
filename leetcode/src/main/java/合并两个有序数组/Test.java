package 合并两个有序数组;

import org.w3c.dom.ls.LSOutput;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/19 16:28
 */
public class Test {
    public static void main(String[] args) {
        int[] nums1 = {1,2,3,4,5};
        int[] nums2 = {0};
        new Solution().merge(nums1, 5, nums2, 0);
    }

    private static class Solution {
        public void merge(int[] nums1, int i, int[] nums2, int i1) {
            if(i1 == 0){
                return;
            }

            int p = i + i1;
            for (int j = 0; j <i+i1; j++) {
                if(i+i1==1){
                    if(i == 0){
                        nums1[i] = nums2[0];
                    }
                    break;
                }else {
                    nums1[i++] = nums2[j];
                    if(i>=p){
                        break;
                    }
                }
            }

            for (int j = 0; j < nums1.length; j++) {
                for (int k = j; k < nums1.length; k++) {
                    if (nums1[k] < nums1[j]) {
                        int t = nums1[k];
                        nums1[k] = nums1[j];
                        nums1[j] = t;
                    }
                }
            }
            for (int j = 0; j < nums1.length; j++) {
                System.out.println(nums1[j]);
            }
        }
    }
}
