package 查找最长不重复字符串;

/**
 * 功能描述:
 *
 * @Class Example1
 * @Author ZYC
 * @Date 2021/5/13 17:16
 * @Version 1.0
 **/
public class Example1 {
    static class Solution {
        public int lengthOfLongestSubstring(String s) {
            // 记录字符上一次出现的位置
            int[] last = new int[128];
            for(int i = 0; i < 128; i++) {
                last[i] = -1;
            }
            int n = s.length();
            int res = 0;
            int start = 0; // 窗口开始位置
            for(int i = 0; i < n; i++) {
                char c = s.charAt(i);
                System.out.println("char ： " + c);
                int index = s.charAt(i);
                System.out.println("index ： " + index);
                start = Math.max(start, last[index] + 1);
                System.out.println("start : " +  start);
                res   = Math.max(res, i - start + 1);
                System.out.println("res : " +  res);
                last[index] = i;
            }

            return res;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int ccwac = solution.lengthOfLongestSubstring("abcabca");
        System.out.println(ccwac);
    }
}
