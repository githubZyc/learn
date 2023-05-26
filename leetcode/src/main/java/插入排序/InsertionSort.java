package 插入排序;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/4/21 15:22
 */
public class InsertionSort {
    public static void main(String[] args) {
        int arrs [] = new int[]{1,3,5,2,9,7,6};
        // i= 1 0位置本身有序不用操作
        for (int i = 1; i < arrs.length; i++) {
            System.out.println(arrs[i]);

            for(int j = i-1; j>=0 && arrs[j] > arrs[j+1];j--){
                //满足交换条件的 当前值与后值做交换
                swap(arrs,j,j+1);
            }
        }

        for (int i = 0; i < arrs.length; i++) {
            System.out.println(arrs[i]);
        }
    }
    public static void swap(int arr[],int i,int j){
        int temp;
        temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
