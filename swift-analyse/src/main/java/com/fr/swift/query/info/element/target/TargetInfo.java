package com.fr.swift.query.info.element.target;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.cal.ResultTarget;

import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public interface TargetInfo {

    /**
     * 整个过程中计算的所有指标的长度，包括所有要展示的指标和一些为了计算而间接计算的指标
     *
     * @return
     */
    int getTargetLength();

    /**
     * 需要聚合的指标，Metric里面包含Aggregator的信息
     * 明细过滤也是在解析SegmentGroupQuery的时候加到Aggregator里面去的
     *
     * @return
     */
    List<Metric> getMetrics();

    /**
     * 要根据聚合结果或者已经计算好的计算指标来计算的计算指标
     *
     * @return
     */
    List<GroupTarget> getGroupTargets();

    /**
     * 最后一步结果汇总的聚合器（结果过滤之后）
     * 对List<ResultTarget>取出来的结果进行汇总，他们的顺序和对应关系在解析查询信息的就确定好了
     *
     * @return
     */
    List<Aggregator> getResultAggregators();

    /**
     * 用于从最后一步计算的中间结果([metrics + groupTargets])中取出查询最后返回的结果
     *
     * @return
     */
    List<ResultTarget> getTargetsForShowList();
}
