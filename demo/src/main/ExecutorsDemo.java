package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/4/13 11:29
 */
public class ExecutorsDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
    }
}
