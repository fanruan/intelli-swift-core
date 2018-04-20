package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.source.SourceKey;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfo extends AbstractQueryInfo<GroupByResultSet> {

    /**
     * 计算过程中的指标长度
     */
    private int targetLength;

    /**
     * 分组表的维度
     */
    private Dimension[] dimensions;
    /**
     * 分组表的聚合维度
     */
    private Metric[] metrics;

    /**
     * 分组表的指标
     */
    private GroupTarget[] targets;

    /**
     * 展开
     */
    private Expander expander;

    public GroupQueryInfo(Cursor cursor, String queryID, SourceKey table, FilterInfo filterInfo, Dimension[] dimensions,
                          Metric[] metrics, GroupTarget[] targets, Expander expander, int targetLength) {
        super(cursor, queryID, table, filterInfo);
        this.dimensions = dimensions;
        this.metrics = metrics;
        this.targets = targets;
        this.expander = expander;
        this.targetLength = targetLength;
    }

    @Override
    public QueryType getType() {
        return QueryType.GROUP;
    }

    public Dimension[] getDimensions() {
        return dimensions;
    }

    public Metric[] getMetrics() {
        return metrics;
    }

    public GroupTarget[] getTargets() {
        return targets;
    }

    public Expander getExpander() {
        return expander;
    }

    public int getTargetLength() {
        return targetLength;
    }

    /**
     * 是否可以分页
     *
     * @return
     */
    public boolean isPagingQuery() {
        // TODO: 2018/4/19 判断计算指标能不能分页
        return !hasResultSort() && !hasMatchFilter();
    }

    private boolean hasResultSort() {
        for (Dimension dimension : dimensions) {
            if (isSortByResult(dimension.getSort())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSortByResult(Sort sort) {
        if (sort == null) {
            return false;
        }
        return sort.getSortType() != SortType.NONE && sort.getTargetIndex() >= dimensions.length;
    }

    private boolean hasMatchFilter() {
        for (Dimension dimension : dimensions) {
            if (isMatchFilter(dimension.getFilter())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMatchFilter(FilterInfo filter) {
        if (filter == null) {
            return false;
        }
        return filter.isMatchFilter();
    }
}
