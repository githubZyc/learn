//编写一个函数来查找字符串数组中的最长公共前缀。 
//
// 如果不存在公共前缀，返回空字符串 ""。 
//
// 
//
// 示例 1： 
//
// 
//输入：strs = ["flower","flow","flight"]
//输出："fl"
// 
//
// 示例 2： 
//
// 
//输入：strs = ["dog","racecar","car"]
//输出：""
//解释：输入不存在公共前缀。 
//
// 
//
// 提示： 
//
// 
// 1 <= strs.length <= 200 
// 0 <= strs[i].length <= 200 
// strs[i] 仅由小写英文字母组成 
// 
// Related Topics 字符串 
// 👍 2158 👎 0

package leetcode.editor.cn;
public class LongestCommonPrefix_14{
    public static void main(String[] args) {
        Solution solution = new LongestCommonPrefix_14().new Solution();
        System.out.println(solution.longestCommonPrefix(new String[]{"hg","hf","ha"}));
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if(null == strs){
            return "";
        }
        //记录第一个字符串 flower
        String ans = strs[0];
        //从数组中第二个开始遍历
        for (int i = 1; i < strs.length; i++) {
            //两个数组的字符串进行比较 从0开始 k为相同的字符的记录值
            int j=0;
            for (;(j<ans.length() && j<strs[i].length());j++){
            if(strs[i].charAt(j) != ans.charAt(j)){
                break;
            }
        }
        ans = ans.substring(0,j);
        if("".equals(ans)){
            return ans;
        }
    }
        return ans;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

} 