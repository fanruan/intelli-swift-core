package com.fr.swift.query.info.element.metric;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.query.info.element.dimension.SwiftColumnProvider;

/**
 * @author pony
 * @date 2017/12/11
 * 度量，聚合的列
 */
public interface Metric extends SwiftColumnProvider {

    /**
     * 这个过滤器对维度groupBy一行对应明细进行过滤。解析的时候把这个filter包装到aggregator里面去。
     * 这么做是因为bi有个功能对groupBy结果指标为空的情况下，也要显示维度。数据库查询有类似的功能吗？
     *
     * @return
     */
    FilterInfo getFilter();

    Aggregator getAggregator();

    MetricType getMetricType();

}
