package com.fr.swift.query.info.detail;

import com.fr.swift.compare.Comparators;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.QueryType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.info.AbstractQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;
import edu.emory.mathcs.backport.java.util.Arrays;

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
     * 排序的顺序
     */
    private IntList sortIndex;
    /**
     * 明细表的指标，目前只支持公式
     */
    private DetailTarget[] targets;

    private SwiftMetaData metaData;

    public DetailQueryInfo(Cursor cursor, String queryId, Dimension[] dimensions, SourceKey table, DetailTarget[] targets, IntList sortIndex, FilterInfo filterInfo, SwiftMetaData metaData) {
        super(queryId, table, filterInfo, Arrays.asList(dimensions));
        this.dimensions = dimensions;
        this.sortIndex = sortIndex;
        this.targets = targets;
        this.metaData = metaData;
    }

    public DetailQueryInfo(String queryId, SourceKey table, FilterInfo filterInfo, List<Dimension> dimensions) {
        super(queryId, table, filterInfo, dimensions);
        // TODO: 2018/6/7 通用情况下，明细的行操作、排序有待定义
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

    public SwiftMetaData getMetaData() {
        return metaData;
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

        private final SwiftSegmentManager localSegmentProvider = SwiftContext.getInstance().getBean("LocalSegmentProvider", SwiftSegmentManager.class);
        private List<Column> columns;

        public DetailSortComparator() {
            columns = new ArrayList<Column>();
            List<Segment> segments = localSegmentProvider.getSegment(getTable());
            if(segments.size() > 0) {
                for (Dimension dimension : dimensions) {
                    columns.add(dimension.getColumn(segments.get(0)));
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
