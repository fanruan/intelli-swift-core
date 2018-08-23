package com.fr.swift.query.builder;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.SortDetailResultQuery;
import com.fr.swift.query.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
public class LocalDetailGroupQueryBuilder implements LocalDetailQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected LocalDetailGroupQueryBuilder() {
    }

    @Override
    public Query<DetailResultSet> buildLocalQuery(DetailQueryInfo info) {
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        List<Dimension> dimensions = info.getDimensions();
        List<Pair<Sort, Comparator>> comparators = null;
        for (Segment segment : segments) {
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            filterInfos.add(new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW));
            List<Pair<Column, IndexInfo>> columns = AbstractLocalGroupQueryBuilder.getDimensionSegments(segment, dimensions);
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            List<Sort> sorts = info.getSorts();
            queries.add(new SortDetailSegmentQuery(info.getFetchSize(), columns,
                    FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)), sorts));
            if (comparators == null) {
                comparators = getComparators(columns, sorts);
                info.setComparators(comparators);
            }
        }
        return new SortDetailResultQuery(info.getFetchSize(), queries, comparators);
    }

    private static List<Pair<Sort, Comparator>> getComparators(List<Pair<Column, IndexInfo>> columnList, List<Sort> sorts) {
        List<Pair<Sort, Comparator>> pairs = new ArrayList<Pair<Sort, Comparator>>();
        for (Sort sort : sorts) {
            // TODO: 2018/7/24 不一定是索引列
            Comparator comparator = columnList.get(sort.getTargetIndex()).getKey().getDictionaryEncodedColumn().getComparator();
            pairs.add(Pair.of(sort, comparator));
        }
        return pairs;
    }

    @Override
    public Query<DetailResultSet> buildResultQuery(List<Query<DetailResultSet>> queries, DetailQueryInfo info) {
        return new SortDetailResultQuery(info.getFetchSize(), queries, info.getTargets(), info.getComparators());
    }
}
