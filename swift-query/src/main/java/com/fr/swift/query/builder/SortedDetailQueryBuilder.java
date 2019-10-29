package com.fr.swift.query.builder;

import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.SortedDetailResultQuery;
import com.fr.swift.query.segment.detail.SortedDetailSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.Segment;
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
 * @author anchore
 * @date 2019/7/6
 */
class SortedDetailQueryBuilder extends DetailQueryBuilder {

    SortedDetailQueryBuilder(DetailQueryInfo detailQueryInfo) {
        super(detailQueryInfo);
    }

    @Override
    Query<DetailQueryResultSet> getSegmentQuery(Segment seg, List<Pair<Column, IndexInfo>> columns, List<FilterInfo> filterInfos) {
        filterInfos.add(new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW));
        return new SortedDetailSegmentQuery(detailQueryInfo.getFetchSize(), columns,
                FilterBuilder.buildDetailFilter(seg, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)), detailQueryInfo.getSorts());
    }

    @Override
    Query<DetailQueryResultSet> getResultQuery(List<Query<DetailQueryResultSet>> queries) {
        return new SortedDetailResultQuery(detailQueryInfo.getFetchSize(), queries, getComparators(detailQueryInfo.getTable(), detailQueryInfo.getSorts()));
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
}