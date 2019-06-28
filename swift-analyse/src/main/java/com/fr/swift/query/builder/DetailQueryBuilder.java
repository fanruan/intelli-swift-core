package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.DetailResultQuery;
import com.fr.swift.query.result.detail.SortedDetailResultQuery;
import com.fr.swift.query.segment.detail.DetailSegmentQuery;
import com.fr.swift.query.segment.detail.SortedDetailSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
class DetailQueryBuilder extends BaseQueryBuilder {

    private SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param bean
     * @return
     */
    Query<DetailQueryResultSet> buildQuery(DetailQueryInfoBean bean) {
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(bean);
        List<Query<DetailQueryResultSet>> queries = new ArrayList<Query<DetailQueryResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        List<Dimension> dimensions = info.getDimensions();
        for (Segment segment : segments) {
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            List<Pair<Column, IndexInfo>> columns = getDimensionSegments(segment, dimensions);
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            if (info.hasSort()) {
                filterInfos.add(new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW));
                List<Sort> sorts = info.getSorts();
                queries.add(new SortedDetailSegmentQuery(info.getFetchSize(), columns,
                        FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)), sorts));
            } else {
                queries.add(new DetailSegmentQuery(info.getFetchSize(), columns,
                        FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND))));
            }
        }
        return info.hasSort() ?
                new SortedDetailResultQuery(info.getFetchSize(), queries, getComparators(info.getTable(), info.getSorts())) :
                new DetailResultQuery(info.getFetchSize(), queries);
    }

    private static List<Pair<Sort, ColumnTypeConstants.ClassType>> getComparators(SourceKey table, List<Sort> sorts) {
        List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators = new ArrayList<Pair<Sort, ColumnTypeConstants.ClassType>>();
        for (Sort sort : sorts) {
            comparators.add(Pair.of(sort, getClassType(table, sort.getColumnKey().getName())));
        }
        return comparators;
    }

    private static ColumnTypeConstants.ClassType getClassType(SourceKey table, String columnName) {
        SwiftMetaDataColumn column;
        try {
            column = SwiftDatabase.getInstance().getTable(table).getMetadata().getColumn(columnName);
        } catch (SQLException e) {
            return Crasher.crash("failed to read metadata of table: " + table.toString(), e);
        }
        return ColumnTypeUtils.getClassType(column);
    }

    private static final DetailQueryBuilder INSTANCE = new DetailQueryBuilder();

    private DetailQueryBuilder() {
    }

    public static DetailQueryBuilder get() {
        return INSTANCE;
    }
}
