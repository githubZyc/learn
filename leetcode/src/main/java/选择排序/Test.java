package 选择排序;

import java.util.Arrays;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/6/20 10:31
 */
public class Test {
    public static void main(String[] args) {
        int[] arr = {101, 11, 3, 4, 17, 6, 7, 8, 9, 10,12};
//        new SelectionSort().selectSort(arr);
        new SelectionSort().eor1();
    }

    private static class SelectionSort {
        public void selectSort(int[] arr) {
            for (int i = 0; i < arr.length - 1; i++){
                int minIndex = i;
                for (int j = i+1; j < arr.length; j++) {
                    if(arr[minIndex]>arr[j]){
                        minIndex = j;
                    }
                }
                swap(arr, i, minIndex);
            }
            System.out.println("排序后：" + Arrays.toString(arr));
        }
        private void swap(int[] arr, int i, int j) {
            //同位异或 赋值   0^0=0   1^0=1   0^1=1   1^1=0
            arr[i] = arr[i] ^ arr[j];
            arr[j] = arr[i] ^ arr[j];
            arr[i] = arr[i] ^ arr[j];
        }

        /**
         * 异或运算 出现奇数次的数
         */
        public void eor1() {
            int[] b = {1,1,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10};
            int eor = 0;
            for (int k = 0; k < b.length; k++) {
                eor = eor ^ b[k];
            }
            System.out.println(eor);
        }

        public void eor2() {
            int[] arr = {1,1,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,13};
            int eor =0;
            for (int k = 0; k < arr.length; k++) {
                eor = eor ^ arr[k];
            }
            int onlyOne = 0;
            int rightOne = eor & (~eor+1); //提出最右边的1
            for (int k = 0; k < arr.length; k++){
                if((arr[k] & rightOne )== 1){
                    onlyOne  = onlyOne ^ arr[k];
                }
            }
            System.out.println(onlyOne + " " + (eor ^ onlyOne));
        }
    }

}
