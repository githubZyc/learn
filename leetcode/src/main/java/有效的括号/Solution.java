package 有效的括号;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 功能描述:
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。
 *
 * 有效字符串需满足：
 *
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 *  
 *
 * 示例 1：
 *
 * 输入：s = "()"
 * 输出：true
 * 示例 2：
 *
 * 输入：s = "()[]{}"
 * 输出：true
 * 示例 3：
 *
 * 输入：s = "(]"
 * 输出：false
 * 示例 4：
 *
 * 输入：s = "([)]"
 * 输出：false
 * 示例 5：
 *
 * 输入：s = "{[]}"
 * 输出：true
 *  
 *
 * 提示：
 *
 * 1 <= s.length <= 104
 * s 仅由括号 '()[]{}' 组成
 *
 * @Class Solution
 * @Author ZYC
 * @Date 2021/8/16 10:31
 * @Version 1.0
 **/
public class Solution {
    private static final Map<Character,Character> ALL_VALID = new HashMap<Character,Character>(4){{
            put('[',']');put('(',')');put('{','}');put('?','?');
    }};
    public static boolean isValid(String s) {
        if(s.length()>0 && !ALL_VALID.containsKey(s.charAt(0))){
            return false;
        }
        LinkedList<Character> stack = new LinkedList<Character>() {{ add('?'); }};
        for(Character c : s.toCharArray()){
            if(ALL_VALID.containsKey(c)){
                stack.addLast(c);
            }else if(!ALL_VALID.get(stack.removeLast()).equals(c)){
                return false;
            }
        }
        return stack.size() == 1;
    }

    public static void main(String[] args) {
        String s = "{[]}";
        boolean valid = isValid(s);
        System.out.println(valid);
    }

}

