package com.example.springboot3.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description
 * 上述说明了Fork/Join框架的需求，我们可以看下Java中是如何实现此框架的。
 *
 * 分割任务，需要一个Fork类来分割任务，有可能任务很大，需要递归分割，直到分割的任务足够小；
 * 执行任务，将分割的小任务分别放到双端队列中，然后启动线程从双端队列中获取任务并执行；
 * 合并结果，小任务的执行结果会放到各自队列中，此时启动一个线程从各个队列中获取结果数据合并成最终结果。
 * Fork/Join使用一下两个类来完成以上三件事情：
 *
 * ForkJoinTask：我们要使用ForkJoin框架，必须首先创建一个ForkJoin任务。它提供在任务中执行fork()和join()操作的机制，通常情况下我们不需要直接继承ForkJoinTask类，而只需要继承它的子类，Fork/Join框架提供了以下两个子类：
 * RecursiveAction：用于没有返回结果的任务。
 * RecursiveTask ：用于有返回结果的任务。
 * ForkJoinPool ：ForkJoinTask需要通过ForkJoinPool来执行，任务分割出的子任务会添加到当前工作线程所维护的双端队列中，
 * * 进入队列的头部。当一个工作线程的队列里暂时没有任务时，它会随机从其他工作线程的队列的尾部获取一个任务。*
 * @date 2024/7/29 10:35
 */
public class CountTask extends RecursiveTask<Long> {
    private static final long THRESHOLD = 5;
    private long start;
    private long end;

    CountTask(){}

    CountTask(long start, long end) {
        this.end = end;
        this.start = start;

    }

    @Override
    protected Long compute() {
        Long sum = 0L;
        if(end - start <=THRESHOLD){
            for (long i = start; i <= end; i++){
                System.out.println(i);
                sum += i;
            }
        }else{
            long mid = (start + end) / 2;
            CountTask left = new CountTask(start, mid);
            CountTask right = new CountTask(mid + 1, end);
            left.fork();
            right.fork();
            //合并计算结果
            sum = left.join() + right.join();
        }
        return sum;
    }

    public static void main(String[] args) {
        CountTask task = new CountTask(1, 10);
        //ForkJoinPool ：ForkJoinTask需要通过ForkJoinPool来执行，
        // 任务分割出的子任务会添加到当前工作线程所维护的双端队列中，进入队列的头部。
        // 当一个工作线程的队列里暂时没有任务时，它会随机从其他工作线程的队列的尾部获取一个任务。
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Long> submit = pool.submit(task);
        try {
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
