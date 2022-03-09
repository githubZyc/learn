package 两数之和;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;

import java.util.*;

/**
 * 功能描述:
 * 题目描述：
 * -给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * -你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 * @Class Test
 * @Author ZYC
 * @Date 2021/5/13 10:23
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        int[] arrays = {2,7,11,15};
        int target = 9;
        int[] ints = twoSum(arrays, target);
        System.out.println(JSONObject.toJSONString(ints));
    }

    public static int[] twoSum(int[] nums, int target) {
        //解题
        //将数组转为map 每个值包括下标 ：{1:0,3:1,5:2,7:3,9:4}
        //目标值减去当前数组的第一个值  8 - 1 = 7
        //将7作为key get map存在的位置 及另外一个值所在的下标
        //1:
        Map<Integer,Integer> result = new HashMap<>(nums.length);
        for(int i=0;i< nums.length;i++){
            result.put(i,nums[i]);
        }

        //2:目标值减去当前数组的第一个值  8 - 1 = 7
        Iterator<Map.Entry<Integer, Integer>> iterator = result.entrySet().iterator();
        int i = 0;
        while(iterator.hasNext()){
            Map.Entry<Integer, Integer> en = iterator.next();
            //第一个值
            Integer first = en.getValue();
            //第一个值下标
            Integer firstIndex = en.getKey();
            //第二个值
            int sec = target - first;
            //第二个值下标
            Integer secIndex = null;
            for (Map.Entry<Integer,Integer> mapEntry:result.entrySet()) {
                if(mapEntry.getValue().equals(sec)){
                    if(!Objects.equals(mapEntry.getKey(),firstIndex)){
                        secIndex = mapEntry.getKey();
                    }
                }
            }
            if(!Objects.isNull(secIndex)){
                int [] res = new int[2];
                System.out.println("第一个值：" + first + "所在下标:" + firstIndex +"第二个值：" + sec +"" +"所在下标：" + secIndex);
                res[i++] =  firstIndex;
                res[i++] = secIndex;
                iterator.remove();
                return res;
            }
        }
        return null;
    }
}
