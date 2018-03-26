package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.compare.Comparators;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public DetailSortComparator getComparator () {
        return new DetailSortComparator();
    }

    protected class DetailSortComparator implements Comparator<Row> {

        private List<Column> columns;
        public DetailSortComparator() {
            columns = new ArrayList<Column>();
            List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(getTarget());
            if(segments.size() > 0) {
                for (int i = 0; i < dimensions.length; i++) {
                    columns.add(segments.get(0).getColumn(dimensions[i].getColumnKey()));
                }
            }
        }
        @Override
        public int compare(Row o1, Row o2) {

            for (int i = 0; i < sortIndex.size(); i++) {
                int c = 0;
                //比较的列先后顺序
                int realColumn = sortIndex.get(i);
                if (dimensions[i].getSort().getSortType() == SortType.ASC) {
                    c = columns.get(realColumn).getDictionaryEncodedColumn().getComparator().compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (dimensions[i].getSort().getSortType() == SortType.DESC) {
                    c = Comparators.reverse(columns.get(realColumn).getDictionaryEncodedColumn().getComparator()).compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
