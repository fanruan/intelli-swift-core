package com.fr.bi.stable.utils;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created by 小灰灰 on 2014/9/29.
 */
public class CubeBaseUtils {

    public static final String JDK_TYPE = System.getProperty("sun.arch.data.model");
    public static final boolean ISX86_32JDK = ComparatorUtils.equals(JDK_TYPE, "32");

    public static int AVAILABLEPROCESSORS = Runtime.getRuntime().availableProcessors();
    public static int THREADPOOLSIZE = Math.max(1, AVAILABLEPROCESSORS) * 2;

    static {
        if (ISX86_32JDK) {
            String info = "Warring：please use 64-bit JDK";
            FRContext.getLogger().info(info);
            System.out.println(info);
        }
    }

    private final static java.util.concurrent.ExecutorService CUBE_EX = java.util.concurrent.Executors.newFixedThreadPool(THREADPOOLSIZE);

    private final static java.util.concurrent.ExecutorService CALCULATOR_EX = java.util.concurrent.Executors.newFixedThreadPool(THREADPOOLSIZE);


    /**
     * 获取JVM当前可用内存，单位M
     *
     * @return 值
     */
    public static int getAvailableMemSize() {
//		System.gc();
        Runtime rt = Runtime.getRuntime();
        long rr = rt.maxMemory() - (rt.totalMemory() - rt.freeMemory());
        return (int) (rr >> BIBaseConstant.OFFSET20);
    }

    public static int getAllMemSize() {
        Runtime rt = Runtime.getRuntime();
        long rr = rt.maxMemory();
        return (int) (rr >> BIBaseConstant.OFFSET20);
    }

    public static double getMemUsedPercert() {
        return 1 - (double) (getAvailableMemSize() / getAllMemSize());
    }

    /**
     * 执行线程
     *
     * @param c 集合
     * @return list集合
     * @throws InterruptedException
     */
    public static List invokeCalculatorThreads(Collection c) throws InterruptedException {
        return CALCULATOR_EX.invokeAll(c);
    }

    /**
     * 执行线程
     *
     * @param c 集合
     * @return list集合
     * @throws InterruptedException
     */
    public static List invokeCubeThreads(Collection c) throws InterruptedException {
        return CUBE_EX.invokeAll(c);
    }

    public static GroupValueIndex AND(GroupValueIndex filter_gvi, GroupValueIndex bind_filter_gvi) {
        throw new UnsupportedOperationException();
    }

    public static void deleteALLFolder(File file) {
        throw new UnsupportedOperationException();
    }
}