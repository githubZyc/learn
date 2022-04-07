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
public class PalindromeNumber_9{
    public static void main(String[] args) {
        Solution solution = new PalindromeNumber_9().new Solution();
        System.out.println(solution.isPalindrome(10));
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPalindrome(int x) {
        // 121 2 121     22
        String a = String.valueOf(x);
        if(a.length() <= 1){
            return false;
        }
        //结束位置
        int i = a.length() / 2 + 1;
        StringBuilder r = new StringBuilder("");
        for (int i1 = i; i1 > 0; i1--) {
            char c = a.charAt(i1);
            r.append(c);
        }
        return a.substring(0,i).equals(r.toString());
    }

}
//leetcode submit region end(Prohibit modification and deletion)

} 