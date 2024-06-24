package 移除元素;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/18 14:16
 */
public class Test {
    public static void main(String[] args) {
        int[] nums = {0,1,2,2,3,0,4,2};
        int val = 2;
        int i = new Solution().removeElement(nums, val);
        System.out.println(i);
    }

    private static class Solution {
        public int removeElement(int[] nums, int val) {
            if (nums.length ==0) return 0;
            int j = 0;
            for (int i = 0; i <nums.length ; i++) {
                if(nums[i] != val){
                    nums[j++] = nums[i];
                }
            }
            return j;
        }
    }
}
