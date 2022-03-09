package condition;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 功能描述:
 *
 * @Class ConditionTest
 * @Author ZYC
 * @Date 2021/4/15 15:26
 * @Version 1.0
 **/
public class ConditionTest {
    //定义队列长度
    private int queueSize = 10;
    //定义队列容器
    private PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>(queueSize);
    //手动锁
    private Lock lock = new ReentrantLock();
    //条件
    Condition notFull = lock.newCondition();
    //条件
    Condition notEmpty = lock.newCondition();

    //生产者
    class Producer extends Thread{
        @Override
        public void run() {
            produce();
        }

        private void produce() {
            while (priorityQueue.size() == queueSize){
                //队列满

            }
        }
    }

    //消费者
    class Consumer extends Thread{

    }
}
