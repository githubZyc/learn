//给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。 
//
// 请必须使用时间复杂度为 O(log n) 的算法。 
//
// 
//
// 示例 1: 
//
// 
//输入: nums = [1,3,5,6], target = 5
//输出: 2
// 
//
// 示例 2: 
//
// 
//输入: nums = [1,3,5,6], target = 2
//输出: 1
// 
//
// 示例 3: 
//
// 
//输入: nums = [1,3,5,6], target = 7
//输出: 4
// 
//
// 
//
// 提示: 
//
// 
// 1 <= nums.length <= 104 
// -104 <= nums[i] <= 104 
// nums 为 无重复元素 的 升序 排列数组 
// -104 <= target <= 104 
// 
// Related Topics 数组 二分查找 
// 👍 1483 👎 0

package leetcode.editor.cn;
public class SearchInsertPosition_35{
    public static void main(String[] args) {
        Solution solution = new SearchInsertPosition_35().new Solution();
        int i = solution.searchInsert(new int[]{1,3,5,6,7,8}, 2);
        System.out.println(i);
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int searchInsert(int[] nums, int target){
        int len = nums.length;
        //如果目标值不在整个数组中切大于数组所有元素 直接返回最后的位置
        if(nums[len - 1] > target){
            return len;
        }
        int left = 0;
        int right = len - 1;
        while (left<right){
            int mid = (left + right) / 2;
            if(nums[mid] == target){
                return mid;
            }
            if(nums[mid]>target){
                right = mid;
            }
            if(nums[mid]<target){
                left = mid + 1;
            }
        }
        return left;
    }
    public int searchInsert2(int[] nums, int target){
        for(int i =0,k = i+1;i<nums.length;i++,++k){
            if(nums[i] == target){
                return i;
            }
            if(nums[i]>target){
                return i>0?i-1:i;
            }
            if(k>=nums.length){
                return k;
            }
            if(nums[i]<target&&nums[k]>=target){
                return k;
            }
        }
        return 0;
    }

    public int searchInsert1(int[] nums, int target) {
        int start = 0,end = nums.length;
        int half = end / 2;
        if(nums[half] == target){
            return half;
        }
       for(int i= nums[half] < target?half:0;i<=(nums[half] > target?end:nums.length);i++){
           if(nums[i] == target ){
               return i;
           }
           if(i+1>=nums.length){
               if(nums[i]>target){
                    return i;
               }
               return i+1;
           }
          if(nums[i]<target && nums[i+1]>target){
            return i+1;
          }
           if(nums[i]>target && nums[(i-1>0?i-1:i)]>target){
               return i;
           }
       }
       return 0;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

} 