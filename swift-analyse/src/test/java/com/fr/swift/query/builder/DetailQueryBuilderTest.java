package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.DetailResultQuery;
import com.fr.swift.query.result.detail.SortedDetailResultQuery;
import com.fr.swift.query.segment.detail.DetailSegmentQuery;
import com.fr.swift.query.segment.detail.SortedDetailSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-03 11:51
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, QueryInfoParser.class, FilterBuilder.class, SwiftDatabase.class, DetailQueryBuilder.class, ColumnTypeUtils.class})
public class DetailQueryBuilderTest {

    @Test
    public void buildQuery() throws Exception {
        //mock SwiftContext
        mockStatic(SwiftContext.class);
        SwiftContext swiftContext = mock(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(swiftContext);

        //mock SwiftSegmentManager
        SwiftSegmentManager localSegmentProvider = mock(SwiftSegmentManager.class);
        when(swiftContext.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(localSegmentProvider);

        //
        DetailQueryInfoBean bean = mock(DetailQueryInfoBean.class);
        DetailQueryInfo info = mock(DetailQueryInfo.class);
        //mock info.getTable  info.getQuerySegment
        SourceKey table = mock(SourceKey.class);
        when(info.getTable()).thenReturn(table);
        Set queryTarget = PowerMockito.mock(Set.class);
        when(info.getQuerySegment()).thenReturn(queryTarget);

        //mock DetailQueryInfo
        mockStatic(QueryInfoParser.class);
        when(QueryInfoParser.parse(bean)).thenReturn(info);

        //init queries
        List<Query<DetailQueryResultSet>> queries = new ArrayList();

        //mock Segment List<Segments>
        Segment segment = PowerMockito.mock(Segment.class);
        List<Segment> segments = Arrays.asList(segment);
        when(localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment())).thenReturn(segments);

        //mock info.getDimensions()
        List dimensions = PowerMockito.mock(List.class);
        when(info.getDimensions()).thenReturn(dimensions);

        // for
        //init filterInfos
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();

        //mock columns
        List columns = PowerMockito.mock(List.class);
        mockStatic(BaseQueryBuilder.class);
        when(BaseQueryBuilder.getDimensionSegments(segment, dimensions)).thenReturn(columns);

        //info.getFilterInfo!=null  info.hasSort
        FilterInfo filterInfo = PowerMockito.mock(FilterInfo.class);
        when(info.getFilterInfo()).thenReturn(filterInfo);
        filterInfos.add(filterInfo);

        //info.hasSort
        //init info.hasSort=true
        when(info.hasSort()).thenReturn(true);

        //mock SwiftDetailFilterInfo
        SwiftDetailFilterInfo swiftDetailFilterInfo = mock(SwiftDetailFilterInfo.class);
        whenNew(SwiftDetailFilterInfo.class).withArguments(null, null, SwiftDetailFilterType.ALL_SHOW).thenReturn(swiftDetailFilterInfo);
        filterInfos.add(swiftDetailFilterInfo);

        //mock sort init sorts  info.getSorts() = sorts
        Sort sort = PowerMockito.mock(Sort.class);
        List<Sort> sorts = Arrays.asList(sort);
        when(info.getSorts()).thenReturn(sorts);

        //mock info.getFetchSize()
        int fetchSize = 2;
        when(info.getFetchSize()).thenReturn(fetchSize);

        //mock new GeneralFilterInfo
        GeneralFilterInfo generalFilterInfo = mock(GeneralFilterInfo.class);
        whenNew(GeneralFilterInfo.class).withArguments(filterInfos, GeneralFilterInfo.AND).thenReturn(generalFilterInfo);

        //mock detailFilter = FilterBuilder.buildDetailFilter
        DetailFilter detailFilter = PowerMockito.mock(DetailFilter.class);
        mockStatic(FilterBuilder.class);
        when(FilterBuilder.buildDetailFilter(segment, generalFilterInfo)).thenReturn(detailFilter);

        //mock sortedDetailSegmentQuery = new SortedDetailSegmentQuery()
        SortedDetailSegmentQuery sortedDetailSegmentQuery = PowerMockito.mock(SortedDetailSegmentQuery.class);
        whenNew(SortedDetailSegmentQuery.class)
                .withArguments(info.getFetchSize(), columns, FilterBuilder.buildDetailFilter(segment, generalFilterInfo), sorts)
                .thenReturn(sortedDetailSegmentQuery);
        queries.add(sortedDetailSegmentQuery);

        //getComparators getClassType
        //mock columnKey columnName
        ColumnKey columnKey = PowerMockito.mock(ColumnKey.class);
        when(sort.getColumnKey()).thenReturn(columnKey);
        String columnName = "fange";
        when(columnKey.getName()).thenReturn(columnName);

        //getClassType
        //mock column
        SwiftMetaDataColumn column = PowerMockito.mock(SwiftMetaDataColumn.class);
        mockStatic(SwiftDatabase.class);
        SwiftDatabase swiftDatabase = mock(SwiftDatabase.class, Mockito.RETURNS_DEEP_STUBS);
        when(SwiftDatabase.getInstance()).thenReturn(swiftDatabase);
        when(swiftDatabase.getTable(table).getMetadata().getColumn(columnName)).thenReturn(column);

        //mock classtype = ColumnTypeUtils.getClassType
        mockStatic(ColumnTypeUtils.class);
        ColumnTypeConstants.ClassType classType = ColumnTypeConstants.ClassType.INTEGER;
        when(ColumnTypeUtils.getClassType(column)).thenReturn(classType);

        //mock comparators
        List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators = new ArrayList<Pair<Sort, ColumnTypeConstants.ClassType>>();
        comparators.add(Pair.of(sort, classType));

        //mock new SortedDetailSegment
        SortedDetailResultQuery sortedDetailResultQuery = mock(SortedDetailResultQuery.class);
        whenNew(SortedDetailResultQuery.class)
//                .withAnyArguments().thenReturn(sortedDetailResultQuery);
                .withArguments(2, queries, comparators).thenReturn(sortedDetailResultQuery);
        DetailQueryBuilder detailQueryBuilder = DetailQueryBuilder.get();
        assertEquals(sortedDetailResultQuery, detailQueryBuilder.buildQuery(bean));


        //info.hasSort() = false
        when(info.hasSort()).thenReturn(false);
        List<FilterInfo> filterInfos1 = new ArrayList<FilterInfo>();
        filterInfos1.add(filterInfo);
        GeneralFilterInfo generalFilterInfo1 = mock(GeneralFilterInfo.class);
        whenNew(GeneralFilterInfo.class).withArguments(filterInfos1, GeneralFilterInfo.AND).thenReturn(generalFilterInfo1);
        DetailFilter detailFilter1 = mock(DetailFilter.class);
        when(FilterBuilder.buildDetailFilter(segment, generalFilterInfo1)).thenReturn(detailFilter1);

        List<Query<DetailQueryResultSet>> queries1 = new ArrayList<Query<DetailQueryResultSet>>();
        DetailSegmentQuery detailSegmentQuery = mock(DetailSegmentQuery.class);
        whenNew(DetailSegmentQuery.class).withArguments(fetchSize, columns, detailFilter1).thenReturn(detailSegmentQuery);
        queries1.add(detailSegmentQuery);

        DetailResultQuery detailResultQuery = mock(DetailResultQuery.class);
        whenNew(DetailResultQuery.class).withArguments(fetchSize, queries1).thenReturn(detailResultQuery);
        Assert.assertEquals(detailResultQuery, detailQueryBuilder.buildQuery(bean));


    }

}