package 面试题.数组排序;

public class SortArray {
    public static void main(String[] args) {
        Integer[] t1 = new Integer[]{1,2,3,0,0,0};
        Integer[] t2 = new Integer[]{2,4,5};
        sort(t1,t2);   
    }

    private static void sort(Integer[] t1, Integer[] t2) {
        //容器
        Integer[] res = new Integer[t1.length + t2.length];
        int i1=0,i2 = 0;
        while (true){
            int pre = i1++;
            if(t1[pre]>t2[pre]){
                res[pre] = t1[pre];
            }
        }
    }
}
