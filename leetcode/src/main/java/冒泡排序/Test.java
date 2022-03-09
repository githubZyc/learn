package 冒泡排序;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/5/24 9:08
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        int[] arrays = new int[]{1,6,9,10,5,4,11};
        for (int i=0;i<arrays.length;i++){
            for (int j=0;j<arrays.length-1;j++){
                if(arrays[i]<=arrays[j]){
                    int temp = 0;
                    temp = arrays[i];
                    arrays[i] = arrays[j];
                    arrays[j] =  temp;
                }
                System.out.println("arrays[i]：" + arrays[i] + " arrays[j]：" + arrays[j]);
            }
        }
        localPrint(arrays);
    }
    public static void localPrint(int []arrays){
        for (int i = 0; i < arrays.length; i++) {
            System.out.print(arrays[i] + " ");
        }
    }
}
