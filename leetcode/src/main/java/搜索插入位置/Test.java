package 搜索插入位置;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/14 15:37
 */
public class Test {
    public static void main(String[] args) {
        Solution solution = new Solution();
        int[] nums = {2,4,5,6,100};
        int target = 7;
        int i = solution.searchInsert(nums, target);
        System.out.println(i);
    }

    private static class Solution {
        public int searchInsert(int[] nums, int target) {
            for (int i = 0; i < nums.length; i++) {
                if(nums[i] >= target){
                    return i;
                }
            }
            return nums.length;
        }
    }
}
