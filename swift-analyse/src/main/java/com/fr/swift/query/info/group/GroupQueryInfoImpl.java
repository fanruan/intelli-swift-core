package com.fr.swift.query.info.group;

import com.fr.swift.query.QueryType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.AbstractQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfoImpl extends AbstractQueryInfo<NodeResultSet> implements GroupQueryInfo<NodeResultSet> {

    private List<Metric> metrics;
    private List<PostQueryInfo> postQueryInfoList;

    public GroupQueryInfoImpl(String queryId, SourceKey table, DimensionInfo dimensionInfo, TargetInfo targetInfo) {
        super(queryId, table, null, null);
    }

    public GroupQueryInfoImpl(String queryId, SourceKey table, FilterInfo filterInfo, List<Dimension> dimensions,
                              List<Metric> metrics, List<PostQueryInfo> postQueryInfoList) {
        super(queryId, table, filterInfo, dimensions);
        this.metrics = metrics;
        this.postQueryInfoList = postQueryInfoList;
    }

    @Override
    public QueryType getType() {
        return QueryType.GROUP;
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
//        for (Dimension dimension : dimensionInfo.getDimensions()) {
//            if (isSortByResult(dimension.getSort())) {
//                return true;
//            }
//        }
        return false;
    }

    private boolean isSortByResult(Sort sort) {
        if (sort == null) {
            return false;
        }
//        return sort.getSortType() != SortType.NONE && sort.getTargetIndex() >= dimensionInfo.getDimensions().length;
        return false;
    }

    private boolean hasMatchFilter() {
//        for (Dimension dimension : dimensionInfo.getDimensions()) {
//            if (isMatchFilter(dimension.getFilter())) {
//                return true;
//            }
//        }
        return false;
    }

    private boolean isMatchFilter(FilterInfo filter) {
        if (filter == null) {
            return false;
        }
        return filter.isMatchFilter();
    }

    @Override
    public List<Metric> getMetrics() {
        return metrics;
    }

    @Override
    public List<PostQueryInfo> getPostQueryInfoList() {
        return postQueryInfoList;
    }
}
