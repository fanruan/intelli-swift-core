package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.cal.result.detail.SortDetailResultQuery;
import com.fr.swift.cal.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.array.IntList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
public class LocalDetailGroupQueryBuilder implements LocalDetailQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.getInstance().getBean("LocalSegmentProvider", SwiftSegmentManager.class);

    protected LocalDetailGroupQueryBuilder() {
    }

    @Override
    public Query<DetailResultSet> buildLocalQuery(DetailQueryInfo info) {
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegment(info.getTable());
        IntList list = info.getSortIndex();
        List<SortType> sortTypes = new ArrayList<SortType>();
        Dimension[] dimensions = info.getDimensions();
        for (int i = 0; i < dimensions.length; i++) {
            Sort sort = dimensions[i].getSort();
            if (sort.getSortType() != SortType.NONE) {
                sortTypes.add(sort.getSortType());
            }
        }
        for (Segment segment : segments) {
            List<Column> columns = new ArrayList<Column>();
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            filterInfos.add(new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW));
            for (Dimension dimension : dimensions) {
                columns.add(dimension.getColumn(segment));
                if (dimension.getFilter() != null) {
                    filterInfos.add(dimension.getFilter());
                }
            }
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            queries.add(new SortDetailSegmentQuery(columns, FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)), list, sortTypes, info.getMetaData()));
        }
        return new SortDetailResultQuery(queries, info.getComparator(), info.getMetaData());
    }

    @Override
    public Query<DetailResultSet> buildResultQuery(List<Query<DetailResultSet>> queries, DetailQueryInfo info) {
        return new SortDetailResultQuery(queries, info.getTargets(), info.getComparator(), info.getMetaData());
    }
}
