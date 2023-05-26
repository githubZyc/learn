package 冒泡排序;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 16:28
 */
public class Test2 {
    public static void main(String[] args) {
        int a = 1,b=2;
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;
        System.out.println(a  +  "--"  + b);

        int arrays[] = {1,10,3,3,11,27,99,2};
        for (int i = 0; i < arrays.length; i++) {
            for (int j=i+1;j<arrays.length;j++){
                int temp;
                if(arrays[i]>arrays[j]){
                    temp = arrays[i];
                    arrays[i] = arrays[j];
                    arrays[j] = temp;
                }
            }
        }
        for (int i = 0; i < arrays.length; i++) {
            System.out.println(arrays[i]);
        }



    }
}
