package com.lhldyf.gallery.java.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lhldyf
 * @date 2019-06-21 10:59
 */
public class CompletableFutureTest {
    public static void main(String[] args) {
        CompletableFutureTest test = new CompletableFutureTest();
        test.test1();
    }

    public void test1() {
        long start = System.currentTimeMillis();
        // 结果集
        List<String> list = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Integer> taskList = Arrays.asList(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);

        CompletableFuture[] allList = new CompletableFuture[taskList.size()];
        for (int i = 0; i < taskList.size(); i++) {
            Integer integer = taskList.get(i);
            allList[i] = CompletableFuture.supplyAsync(() -> calc(integer), executorService)
                                          .thenApply(h -> Integer.toString(h)).whenComplete((result, e) -> {
                        System.out.println("结果：" + result);
                    });
        }

        CompletableFuture.allOf(allList).thenAccept((x) -> {
            System.out.println("allOF");
        });

        System.out.println("主线程释放");


        // // 全流式处理转换成CompletableFuture[]+组装成一个无返回值CompletableFuture，join等待执行完毕。返回结果whenComplete获取
        // CompletableFuture[] cfs = taskList.stream().map(integer -> CompletableFuture
        //         .supplyAsync(() -> calc(integer), executorService).thenApply(h -> Integer.toString(h))
        //         .whenComplete((s, e) -> {
        //             System.out.println("任务" + s + "完成!result=" + s + "，异常 e=" + e + "," + new Date());
        //             list.add(s);
        //         })).toArray(CompletableFuture[]::new);
        // // 封装后无返回值，必须自己whenComplete()获取
        // CompletableFuture.allOf(cfs).join();
        // System.out.println("list=" + list + ",耗时=" + (System.currentTimeMillis() - start));
    }

    public int calc(Integer i) {
        try {
            if (i == 1) {
                Thread.sleep(3000);//任务1耗时3秒
            } else if (i == 5) {
                Thread.sleep(5000);//任务5耗时5秒
            } else {
                Thread.sleep(1000);//其它任务耗时1秒
            }
            System.out.println("task线程：" + Thread.currentThread().getName() + "任务i=" + i + ",完成！+" + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }
}
