package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntList;

/**
 * @author pony
 * @date 2017/12/11
 */
public class DetailQueryInfo extends AbstractQueryInfo<DetailResultSet> {
    /**
     * 明细表维度，支持分组，排序
     */
    private Dimension[] dimensions;
    /**
     * 公共子表
     */
    private SourceKey target;
    /**
     * 排序的顺序
     */
    private IntList sortIndex;
    /**
     * 明细表的指标，目前只支持公式
     */
    private DetailTarget[] targets;

    public DetailQueryInfo(Cursor cursor, String queryID, Dimension[] dimensions, SourceKey target, DetailTarget[] targets, IntList sortIndex, FilterInfo filterInfo) {
        super(cursor, queryID, filterInfo);
        this.dimensions = dimensions;
        this.target = target;
        this.sortIndex = sortIndex;
        this.targets = targets;
    }

    public Dimension[] getDimensions() {
        return dimensions;
    }

    public SourceKey getTarget() {
        return target;
    }

    public DetailTarget[] getTargets() {
        return targets;
    }

    public IntList getSortIndex() {
        return sortIndex;
    }

    @Override
    public QueryType getType() {
        return QueryType.DETAIL;
    }

    public boolean hasSort() {
        for (Dimension dimension : dimensions) {
            if (dimension.getSort() != null && dimension.getSort().getSortType() != SortType.NONE) {
                return true;
            }
        }
        return false;
    }
}
