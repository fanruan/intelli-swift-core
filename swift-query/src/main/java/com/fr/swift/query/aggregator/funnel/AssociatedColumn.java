package com.fr.swift.query.aggregator.funnel;

/**
 * @author yee
 * @date 2019-08-29
 */
public interface AssociatedColumn {

    /**
     * 取Index
     *
     * @param columnIdx
     * @param row
     * @return
     */
    int getIndex(int columnIdx, int row);

    /**
     * 字典数
     *
     * @return
     */
    int dictSize();
}
