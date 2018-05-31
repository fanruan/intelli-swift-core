package com.fr.swift.query.info.target;


/**
 * Created by pony on 2017/12/13.
 * 分组表的指标
 */
public interface GroupTarget extends Target {

    /**
     * 过滤之后要再次计算的计算指标
     *
     * @return
     */
    boolean isRepeatCal();
}
