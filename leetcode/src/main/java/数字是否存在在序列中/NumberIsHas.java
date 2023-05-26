package 数字是否存在在序列中;

import java.util.Arrays;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description 给你N个自然数 1<N<100，每个自然数的范围是1-100，最快的速度判断某一个数是否在这N个数内。
 * @date 2023/4/19 08:44
 */
public class NumberIsHas {
    public static void main(String[] args) {
        //查找这些值是否存在
        int find = 5;

        //N 个自然数 初始化100个数组值
        int []all = new int[100];

        int []numbers = new int[]{10,50,60,15,8};
        for (int i = 0; i < numbers.length; i++) {
            all[numbers[i]] = 1;
        }

        System.out.println("数组中是否存在：" + (all[find] == 1));
        System.out.println(20%10);

        int i = hashCode("9");
        System.out.println(i);
    }

    public static int hashCode(Object key){
        int h = key.hashCode();
        return (h ^ (h >>> 16) & (10-1));
    }

}
