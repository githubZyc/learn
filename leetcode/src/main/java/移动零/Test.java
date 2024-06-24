package 移动零;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/18 15:49
 */
public class Test {
    public static void main(String[] args) {
        int[] nums = {0,1,0,3,12};
        Solution solution = new Solution();
        int[] ints = solution.moveZeroes(nums);
        for (int num : ints) {
            System.out.println(num);
        }
    }

    private static class Solution {
        public int[] moveZeroes(int[] nums) {
            if(nums.length == 0){
                return nums;
            }
            int j=0;
            for (int i = 0; i < nums.length; i++){
                if (nums[i] !=0){
                    int tmp = nums[i];
                    nums[i] = nums[j];
                    nums[j++] = tmp;
                }
            }
            return nums;
        }
    }
}
