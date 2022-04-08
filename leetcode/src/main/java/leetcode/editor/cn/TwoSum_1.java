//给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那 两个 整数，并返回它们的数组下标。 
//
// 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。 
//
// 你可以按任意顺序返回答案。
//
//
//
// 示例 1： 
//
// 
//输入：nums = [2,7,11,15], target = 9
//输出：[0,1]
//解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
//
//
// 示例 2：
//
// 
//输入：nums = [3,2,4], target = 6
//输出：[1,2]
// 
//
// 示例 3： 
//
// 
//输入：nums = [3,3], target = 6
//输出：[0,1]
// 
//
// 
//
// 提示： 
//
// 
// 2 <= nums.length <= 104 
// -109 <= nums[i] <= 109 
// -109 <= target <= 109 
// 只会存在一个有效答案 
// 
//
// 进阶：你可以想出一个时间复杂度小于 O(n2) 的算法吗？ 
// Related Topics 数组 哈希表 
// 👍 13693 👎 0

package leetcode.editor.cn;

import java.util.*;

public class TwoSum_1{
    public static void main(String[] args) {
        Solution solution = new TwoSum_1().new Solution();
        int[] ints = solution.twoSum(new int[]{1,3,5}, 6);
        System.out.println(Arrays.toString(ints));
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] twoSum2(int[] nums, int target) {
        //创建容器
        int indexs[]= new int[2];
        //将数组中的数添加到hash中
        Map<Integer, Integer> hash = new HashMap<>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            hash.put(nums[i],i);
        }
        for (int i = 0; i < nums.length; i++) {
            //遍历时排斥自身
            if(hash.containsKey(target - nums[i]) && hash.get(target - nums[i]) != i){
                indexs[0] = i;
                indexs[1] = hash.get(target - nums[i]);
                return indexs;
            }
        }
        return null;
    }

    public int[] twoSum(int[] nums, int target) {
        //创建容器
        int indexs[]= new int[2];
        //将数组中的数添加到hash中
        Map<Integer, Integer> hash = new HashMap<>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            boolean containsKey = hash.containsKey(nums[i]);
            if(containsKey){
                indexs[0] = i;
                indexs[1] = hash.get(nums[i]);
                return indexs;
            }
            //hash 中key 存储做差后的值 已备后边的循环中找到当时的数组下标位置。 value 为当时的下标
            hash.put(target - nums[i],i);
        }
        return null;
    }

    public int[] twoSum1(int[] nums, int target) {
        //解题
        //将数组转为map 每个值包括下标 ：{1:0,3:1,5:2,7:3,9:4}
        //目标值减去当前数组的第一个值  8 - 1 = 7
        //将7作为key get map存在的位置 及另外一个值所在的下标
        //1:
        Map<Integer, Integer> result = new HashMap<>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            result.put(i, nums[i]);
        }

        //2:目标值减去当前数组的第一个值  8 - 1 = 7
        Iterator<Map.Entry<Integer, Integer>> iterator = result.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> en = iterator.next();
            //第一个值
            Integer first = en.getValue();
            //第一个值下标
            Integer firstIndex = en.getKey();
            //第二个值
            int sec = target - first;
            //第二个值下标
            Integer secIndex = null;
            for (Map.Entry<Integer, Integer> mapEntry : result.entrySet()) {
                if (mapEntry.getValue().equals(sec)) {
                    if (!Objects.equals(mapEntry.getKey(), firstIndex)) {
                        secIndex = mapEntry.getKey();
                    }
                }
            }
            if (!Objects.isNull(secIndex)) {
                int[] res = new int[2];
                System.out.println("第一个值：" + first + "所在下标:" + firstIndex + "第二个值：" + sec + "" + "所在下标：" + secIndex);
                res[i++] = firstIndex;
                res[i++] = secIndex;
                iterator.remove();
                return res;
            }
        }
        return null;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

} 