package 移除数组中的重复元素;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/18 13:57
 */
public class Test {
    public static void main(String[] args) {
        int[] nums = {0,0,1,1,1,2,2,3,3,4};
        int i = new Solution().removeDuplicates(nums);
        System.out.println(i);
    }

    private static class Solution {
        public int removeDuplicates(int[] nums) {
            if (nums.length==0){
                return 0;
            }
            int j=0;
            for (int i = 1; i < nums.length; i++){
                if(nums[i]!=nums[j]){
                    j++;
                    nums[j]=nums[i];
                }
            }
            return j+1;
        }
    }
}
