package com.fr.bi.cal.analyze.cal.thread;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.report.key.SummaryCalculator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Connery on 2014/12/16.
 */
public class EvaluateSummaryValuePool {

    private static ExecutorService executorService = Executors.newFixedThreadPool(BIBaseConstant.THREADPOOLSIZE);


    /**
     * 求取数值，加入线程池。
     *
     * @param node      节点
     * @param gcvs      分组连接值
     * @param targetKey 目标key
     */
    public static Future evaluateValue(SummaryCalculator summaryCalculator) {
        return executorService.submit(summaryCalculator);
    }

    public static void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}