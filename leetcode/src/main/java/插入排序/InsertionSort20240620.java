package 插入排序;

import java.util.Arrays;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/20 15:04
 */
public class InsertionSort20240620 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        insertionSort(arr);
    }

    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++){

        }
        System.out.println(Arrays.toString(arr));
    }

    public static void swap(int[] arr, int i, int j) {
       arr[i] = arr[i] ^ arr[j];
       arr[j] = arr[i] ^ arr[j];
       arr[i] = arr[i] ^ arr[j];
    }
}
