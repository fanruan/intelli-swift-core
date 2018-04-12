package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetDeep;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.source.SourceKey;

import java.util.TreeSet;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfo extends AbstractQueryInfo<GroupByResultSet> {
    //分组表的维度
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
                          Metric[] metrics, GroupTarget[] targets, Expander expander) {
        super(cursor, queryID, table, filterInfo);
        this.dimensions = dimensions;
        this.metrics = metrics;
        this.targets = targets;
        this.expander = expander;
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

    /**
     * 是否可以分页
     *
     * @return
     */
    public boolean isPagingQuery() {
        return !hasResultSort() && !hasMatchFilter() && getMaxDeep() != TargetDeep.ALL_GROUP;
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

    /**
     * 有非行间计算的指标
     *
     * @return
     */
    private TargetDeep getMaxDeep() {
        TreeSet<TargetDeep> set = new TreeSet<TargetDeep>();
        for (GroupTarget target : targets) {
            set.add(target.getTargetDeep());
        }
//        return set.last();
        return set.isEmpty() ? TargetDeep.ROW : set.last();
    }
}
