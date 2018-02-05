package com.fr.swift.query.adapter.target;


/**
 * Created by pony on 2017/12/13.
 * 分组表的指标
 */
public interface GroupTarget extends Target {
    /**
     * 指标需要的计算深度
     * @return
     */
    TargetDeep getTargetDeep();
}
