package 整数反转;

/**
 * 功能描述:
 * 给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。
 * <p>
 * 如果反转后整数超过 32 位的有符号整数的范围 [−231,  231 − 1] ，就返回 0。
 * 2^31-1=2147483647, -2^31=-2147483648
 * <p>
 * 假设环境不允许存储 64 位整数（有符号或无符号）。
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：x = 123
 * 输出：321
 * 示例 2：
 * <p>
 * 输入：x = -123
 * 输出：-321
 * 示例 3：
 * <p>
 * 输入：x = 120
 * 输出：21
 * 示例 4：
 * <p>
 * 输入：x = 0
 * 输出：0
 *
 * @Class Solution
 * @Author ZYC
 * @Date 2021/8/12 11:28
 * @Version 1.0
 **/
public class Solution {
    public static int reverse1(int x) {
        StringBuilder result = new StringBuilder("");
        if (x >= Integer.MAX_VALUE || x <= Integer.MIN_VALUE) {
            return 0;
        }

        String s = String.valueOf(x);
        boolean b = s.startsWith("-");

        char[] chars = s.toCharArray();
        for (int i = chars.length - 1; b ? i >= 1 : i >= 0; i--) {
            result.append(chars[i]);
        }

        String s1 = result.toString();
        try{
            Integer.valueOf(s1);
        }catch (Exception e){
            return 0;
        }
        return b ? -Integer.valueOf(s1) : Integer.valueOf(s1);
    }

    public static void main(String[] args) {
        int reverse = reverse(1534236469);
        System.out.println(reverse);

    }

    private static int reverse(int x) {
        long result = 0L;
        while (x!=0){
            int temp = x%10;
            result = result*10 + temp;
            x = x/10;
        }
        //巧妙使用 int 强转越界处理
        return (int)result == result ? (int)result : 0;
    }

    private static int reverse2(int x) {
        //2^31-1=2147483647, -2^31=-2147483648
        int res = 0;
        while(x!=0) {
            //每次取末尾数字
            int tmp = x%10;
            //判断是否 大于 最大32位整数 214748364 未去除尾数的 结果
            if (res>214748364 || (res==214748364 && tmp>7)) {
                return 0;
            }
            //判断是否 小于 最小32位整数 -214748364 未去除尾数的 结果
            if (res<-214748364 || (res==-214748364 && tmp<-8)) {
                return 0;
            }
            res = res*10 + tmp;
            x = x /10;
        }
        return res;
    }


}
