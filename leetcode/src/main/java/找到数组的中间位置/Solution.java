package 找到数组的中间位置;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/14 15:03
 */
public class Solution {

    public int findMiddleIndex(int[] nums) {
        if(nums.length == 1){
            return 0;
        }

        int sum = 0;
        int countRight = 0, countLeft = 0;
        for (int i = 0; i < nums.length; i++) {
            countRight+=nums[i];
        }

        for (int i = 0; i < nums.length; i++) {
            if (i > 0){
                countLeft += nums[i-1];
            }
            countRight -= nums[i];
            if (countLeft == countRight) {
                return i;
            }
        }
        return -1;
    }
}
