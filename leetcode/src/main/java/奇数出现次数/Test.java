package 奇数出现次数;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description
 * @date 2023/4/21 14:31
 */
public class Test {

    public static void main(String[] args) {
        // 1
        {
            /**
             * 一个数组中只有一个数出现了奇数次 ，其他出现的都是偶数次  这个数是那个？
             */
            int []s = new int[]{1,2,2,1,2,2,3,3,3};
            int eor = 0;
            for (int i : s) {
                eor = eor ^ i;
            }
            System.out.println(eor);
        }

        // 2
        {
            /**
             * 一个数组中只有两个数出现了奇数次 ，其他出现的都是偶数次  这个数是那个？
             */
            int []s = new int[]{1,2,2,1,2,2,3,3,3,5};
            int eor = 0;
            for (int i : s) {
                eor = eor ^ i;
            }

            // eor = a ^ b
            // eor !=0
            // eor必然有一个位置上是1
            int rightOne = eor & (~eor + 1);
            //eor
            int onlyOne = 0;
            for (int cur : s){
                if((cur & rightOne) == 0){
                    onlyOne ^= cur;
                }
            }
            System.out.println(onlyOne + " -- " + (eor ^ onlyOne));
        }
    }
}
