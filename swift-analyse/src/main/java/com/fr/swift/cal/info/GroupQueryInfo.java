package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.DimensionInfo;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.row.GroupByResultSet;
import com.fr.swift.source.SourceKey;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfo extends AbstractQueryInfo<GroupByResultSet> {

    private DimensionInfo dimensionInfo;
    private TargetInfo targetInfo;

    public GroupQueryInfo(String queryId, SourceKey table, DimensionInfo dimensionInfo, TargetInfo targetInfo) {
        super(dimensionInfo.getCursor(), queryId, table, dimensionInfo.getFilterInfo());
        this.dimensionInfo = dimensionInfo;
        this.targetInfo = targetInfo;
    }

    @Override
    public QueryType getType() {
        return QueryType.GROUP;
    }

    public DimensionInfo getDimensionInfo() {
        return dimensionInfo;
    }

    public TargetInfo getTargetInfo() {
        return targetInfo;
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
        for (Dimension dimension : dimensionInfo.getDimensions()) {
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
        return sort.getSortType() != SortType.NONE && sort.getTargetIndex() >= dimensionInfo.getDimensions().length;
    }

    private boolean hasMatchFilter() {
        for (Dimension dimension : dimensionInfo.getDimensions()) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupQueryInfo that = (GroupQueryInfo) o;

        if (dimensionInfo != null ? !dimensionInfo.equals(that.dimensionInfo) : that.dimensionInfo != null)
            return false;
        return targetInfo != null ? targetInfo.equals(that.targetInfo) : that.targetInfo == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dimensionInfo != null ? dimensionInfo.hashCode() : 0);
        result = 31 * result + (targetInfo != null ? targetInfo.hashCode() : 0);
        return result;
    }
}
