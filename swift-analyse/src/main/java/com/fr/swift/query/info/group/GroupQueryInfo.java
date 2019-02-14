package com.fr.swift.query.info.group;

import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/5/29.
 */
public interface GroupQueryInfo<T extends QueryResultSet> extends SingleTableQueryInfo<T> {

    /**
     * 明细聚合器
     *
     * @return
     */
    List<Metric> getMetrics();

    /**
     * 这边包含对当前聚合结果二维表的所有增删改操作，包括配置类计算、结果过滤、结果聚合、结果排序等
     * 那什么时候需要嵌套查询呢？要重新构建树结构结果集并做相关PostQuery的情况下吗？
     *
     * @return
     */
    List<PostQueryInfo> getPostQueryInfoList();
}
