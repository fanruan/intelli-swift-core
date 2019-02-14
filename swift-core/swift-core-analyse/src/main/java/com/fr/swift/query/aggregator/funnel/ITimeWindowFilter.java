package com.fr.swift.query.aggregator.funnel;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface ITimeWindowFilter {

    void init();

    void setDictSize(int size);

    /**
     * 添加一条明细记录
     * @param event 事件
     * @param timestamp 时间戳
     * @return 如果已经得到完整步骤，则返回true，否则返回false
     */
    void add(int event, int timestamp, int dateDictIndex, int associatedValue, Object groupValue);

    /**
     * 返回结果
     *
     * @return
     */
    List<IHead> getResult();

    void reset();
}
