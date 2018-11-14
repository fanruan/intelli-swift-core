package com.fr.swift.setting;

/**
 * Created by Hiram on 2015/3/18.
 */
public interface PerformancePlugManagerInterface {
    /**
     * 生成cube时是否用堆外内存的int数组
     *
     * @return
     */
    boolean isDirectGenerating();

    boolean isDiskSort();

    long getDiskSortDumpThreshold();
}
