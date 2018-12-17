package com.fr.swift.source.alloter;

/**
 * 行分析器，例如实现hash算法
 *
 * @author yee
 * @date 2018-12-17
 */
public interface RowAnalyzer {
    /**
     * 根据RowInfo计算SegOrder
     *
     * @param row
     * @return
     */
    int analyseHistory(RowInfo row);

    /**
     * 根据RowInfo计算SegOrder
     *
     * @param row
     * @return
     */
    int analyseRealTime(RowInfo row);

    /**
     * 根据RowInfo计算SegOrder
     *
     * @param row
     * @return
     */
    int analyseCollate(RowInfo row);
}
