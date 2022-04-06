package 面试题.数组排序;

import java.util.Arrays;

public class SortArray {
    public static void main(String[] args) {
        Integer[] t1 = new Integer[]{5,7,9,0,0,0};
        Integer[] t2 = new Integer[]{1,2,3};
        sort(t1,t2);   
    }

    private static void sort(Integer[] t1, Integer[] t2) {
        //容器
        Integer[] res = new Integer[t1.length + t2.length];
        //a1 为t1的下标，a2 = t2的下标，k= 新数组
        int a1=0,a2=0,k =0;
        //结束条件为当一个数组遍历完就跳出循环
        while (a1<t1.length && a2<t2.length){
            if(t1[a1]<=t2[a2]){
                res[k] = t1[a1];
                a1++;
            }else{
                res[k] = t2[a2];
                a2++;
            }
            k++;
        }
        while (a1<t1.length){
            res[k++] = t1[a1++];
        }
        while (a2<t2.length){
            res[k++] = t2[a2++];
        }
        System.out.println(Arrays.toString(res));
    }
}
