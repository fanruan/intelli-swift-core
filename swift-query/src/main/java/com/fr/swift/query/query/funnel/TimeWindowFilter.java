package com.fr.swift.query.query.funnel;

import com.fr.swift.util.function.Function;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface TimeWindowFilter {

    void init();

    void setDictSize(int size);

    /**
     * 添加一条明细记录
     *
     * @param event     事件
     * @param timestamp 时间戳
     * @return 如果已经得到完整步骤，则返回true，否则返回false
     */
    void add(int event, long timestamp, Function<Integer, Integer> associatedValue, Object groupValue, int row);

    /**
     * 返回结果
     *
     * @return
     */
    List<IHead> getResult();

    void reset();
}
