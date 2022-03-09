package 面试题.交替输出;

public class Test {
    static volatile boolean update = true;
    public static void main(String[] args) {
        char[] t1 = new char[]{'a','b','c'};
        char[] t2 = new char[]{'1','2','3','4','5','6'};

        Thread thread = new Thread(()->{
            for (int i=0;i<t1.length;i++){
                while (true){
                    if(update){
                        System.out.println(t1[i]);
                        getUpdate(false);
                        break;
                    }
                }
            }
        });

        Thread thread2 = new Thread(()->{
            for (int i=0;i<t2.length;i++){
                while(true){
                    if(!update){
                        System.out.println(t2[i]);
                        getUpdate(true);
                        break;
                    }
                }
            }
        });
        thread.start();
        thread2.start();
    }

    public static synchronized void getUpdate(boolean flag) {
         update = flag;
    }
}
