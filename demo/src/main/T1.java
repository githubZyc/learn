package main;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/4/13 11:09
 */
public class T1 {
    static class Thread1 implements Runnable{

        //运行
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
            //阻塞
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        //线程创建
        Thread thread = new Thread(new Thread1());
        //线程就绪
        thread.start(); //真正的启动一个线程
        //线程销毁
        thread.run(); //只是打印当前主线程的 线程名称，
    }
}
