package com.fr.swift.query.builder;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.Query;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.result.detail.NormalDetailResultQuery;
import com.fr.swift.query.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
public class LocalDetailNormalQueryBuilder implements LocalDetailQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected LocalDetailNormalQueryBuilder() {
    }

    @Override
    public Query<DetailResultSet> buildLocalQuery(DetailQueryInfo info) {
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegment(info.getTable());
        for (Segment segment : segments) {
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            Dimension[] dimensions = info.getDimensions().toArray(new Dimension[info.getDimensions().size()]);
            List<Column> columns = new ArrayList<Column>();
            for (Dimension dimension : dimensions) {
                columns.add(dimension.getColumn(segment));
//                if (dimension.getFilter() != null) {
//                    filterInfos.add(dimension.getFilter());
//                }
            }
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            queries.add(new NormalDetailSegmentQuery(columns, FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)), info.getMetaData()));
        }
        return new NormalDetailResultQuery(queries, info.getMetaData());
    }

    @Override
    public Query<DetailResultSet> buildResultQuery(List<Query<DetailResultSet>> queries, DetailQueryInfo info) {
        return new NormalDetailResultQuery(queries, info.getTargets(), info.getMetaData());
    }
}
