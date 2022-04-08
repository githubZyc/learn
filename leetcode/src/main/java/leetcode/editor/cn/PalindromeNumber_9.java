//给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。 
//
// 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。 
//
// 
// 例如，121 是回文，而 123 不是。 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：x = 121
//输出：true
// 
//
// 示例 2： 
//
// 
//输入：x = -121
//输出：false
//解释：从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
// 
//
// 示例 3： 
//
// 
//输入：x = 10
//输出：false
//解释：从右向左读, 为 01 。因此它不是一个回文数。
// 
//
// 
//
// 提示：
//
// 
// -231 <= x <= 231 - 1 
// 
//
// 
//
// 进阶：你能不将整数转为字符串来解决这个问题吗？ 
// Related Topics 数学 
// 👍 1928 👎 0

package leetcode.editor.cn;

public class PalindromeNumber_9 {
    public static void main(String[] args) {
        Solution solution = new PalindromeNumber_9().new Solution();
        System.out.println(solution.isPalindrome(1000030001));
    }


    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public boolean isPalindrome(int x) {
            int newX=0,origin = x;
            while (x>0){
                int i = x % 10;
                x = x/10;
                newX = newX*10 + i;
            }
            return newX == origin;
        }

        public boolean isPalindrome1(int x) {
            String a = String.valueOf(x);
            if (a.length() <= 1) {
                return true;
            }
            if (a.endsWith("0")) {
                return false;
            }
            //结束位置
            int il = a.length() % 2 == 0 ? a.length() / 2 : a.length() / 2 + 1;
            StringBuilder r = new StringBuilder("");
            for (int i = a.length() - 1; (i >= (a.length() % 2 == 0 ? il : il - 1)); i--) {
                char c = a.charAt(i);
                r.append(c);
            }
            return a.substring(0, il).equals(r.toString());
        }

    }
//leetcode submit region end(Prohibit modification and deletion)

} 