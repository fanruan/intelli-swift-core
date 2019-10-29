package com.fr.swift.query.info.element.target;

import com.fr.swift.query.info.bean.type.cal.CalTargetType;
import com.fr.swift.query.info.element.dimension.QueryColumn;

/**
 * Created by pony on 2017/12/11.
 * 指标，对聚合结果的处理
 */
public interface Target extends QueryColumn {

    /**
     * 计算参数的索引
     * eg：处理的中间结果为result = [2, 3, 1, null, null]，假设当前是公式计算指标，paramIndexes = [0, 2]
     * 则result[0]和result[2]为公式计算的参数
     *
     * @return
     */
    int[] paramIndexes();

    /**
     * 计算结果保存位置的索引
     * eg：result[resultIndex] = result[0] + result[2]
     *
     * @return
     */
    int resultIndex();

    /**
     * 计算指标类型
     *
     * @return
     */
    CalTargetType type();
}
