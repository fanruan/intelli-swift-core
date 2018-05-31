package com.fr.swift.query.info;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.metric.Metric;
import com.fr.swift.query.info.target.GroupTarget;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/5/29.
 */
public interface GroupQueryInfo<T extends SwiftResultSet> extends SingleTableQueryInfo<T> {

    /**
     * 明细聚合器
     *
     * @return
     */
    List<Metric> getMetrics();

    /**
     * 基于明细聚合结果的计算。这个通过query嵌套来实现，也就是iterator模型
     *
     * @return
     */
    List<GroupTarget> getPostCalculationInfo();

    /**
     * 对明细聚合结果的二维表进行过滤，类似数据库的having语句。bi相关的过滤功能只处理到最后一个维度的过滤，也就是只能按行过滤
     *
     * @return
     */
    FilterInfo getHavingFilter();
}
