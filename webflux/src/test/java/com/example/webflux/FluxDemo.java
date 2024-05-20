package com.example.webflux;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/3/11 14:27
 */
public class FluxDemo {
    public static void main(String[] args) {
        //doOnxxx api定义：要感知某个流的时间，写在这个流的后面 新流的前面
        //doOnNext 每个数据 （流的数据）到达的时候触发
        //dOnEach 每个元素（流的数据和信号）到达时触发
        Flux<Integer> flux = Flux.just(1,23,4,5)
                .delayElements(Duration.ofSeconds(1L))
                .doOnComplete(()->{
            System.out.println("打印完成");
        }).doOnCancel(()->{
            System.out.println("取消了不干了");
        });
        flux.subscribe(System.out::println);
//        subscribe.dispose();
    }
}
