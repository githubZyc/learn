package chatgpt;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/5/20 14:51
 */
public class Fibonacci1Test {

    public static int fibonacci1(int n){
        if(n<=1){
            return n;
        }
        return fibonacci1(n-1) + fibonacci1(n-2);
    }

    public static int fibonacci2(int n){
      if(n<=1){
          return 1;
      }
      int a=1,b=1;
      for(int i = 2;i<n;i++){
          int sum = a+b;
          a=b;
          b= sum;

      }
      return b;
    }


    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++){
//            System.out.print("\t"+ fibonacci1(i));
//        }
//
//        for (int i = 0; i < 10; i++){
//            System.out.print("\t"+ fibonacci2(i));
//        }
        System.out.println(fibonacci2(4));
    }






}
