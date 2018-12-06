package com.fr.swift.index;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.IndexInfoImpl;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestCubeData {

    public static void prepare(DataSource dataSource, Class<?> clazz) throws Exception {
//        SwiftCubePathService service = SwiftContext.get().getBean(SwiftCubePathService.class);
//        service.setSwiftPath(TestResource.getRunPath(clazz));
//        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(dataSource.getSourceKey().getId());
//        SwiftSourceTransfer transfer = DefaultSourceTransferProvider.createSourceTransfer(dataSource);
//        SwiftResultSet resultSet = transfer.createResultSet();
//        Inserter inserter = new HistoryBlockInserter(dataSource);
//        inserter.insertData(resultSet);
//
//        List<Segment> segments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(dataSource.getSourceKey());
//        for (String fieldName : dataSource.getMetadata().getFieldNames()) {
//            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(fieldName), segments);
//            columnIndexer.work();
//        }
    }

    public static List<Pair<Column, IndexInfo>> getDimensions(Segment segment, List<String> columnNames) {
        List<Pair<Column, IndexInfo>> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(Pair.of(segment.getColumn(new ColumnKey(columnName)), new IndexInfoImpl(true, false)));
        }
        return columns;
    }

    public static List<Column> getColumns(Segment segment, List<String> columnNames) {
        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(segment.getColumn(new ColumnKey(columnName)));
        }
        return columns;
    }

    public static Map<RowIndexKey<Object[]>, ImmutableBitMap> groupBy(List<DetailColumn> detailColumns, RowTraversal filter) {
        Map<RowIndexKey<Object[]>, IntList> map = new HashMap<>();
        filter.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object[] values = new Object[detailColumns.size()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = detailColumns.get(i).get(row);
                }
                RowIndexKey key = new RowIndexKey(values);
                IntList list = map.get(key);
                if (list == null) {
                    list = IntListFactory.createIntList();
                    map.put(key, list);
                }
                list.add(row);
            }
        });
        Map<RowIndexKey<Object[]>, ImmutableBitMap> result = new HashMap<>();
        for (Map.Entry<RowIndexKey<Object[]>, IntList> entry : map.entrySet()) {
            result.put(entry.getKey(), BitMaps.newImmutableBitMap(entry.getValue()));
        }
        return result;
    }

    public static Set<Row> aggregate(Map<RowIndexKey<Object[]>, ImmutableBitMap> map,
                                     List<Column> metrics, List<Aggregator> aggregators) {
        Set<Row> rows = new HashSet<>();
        for (Map.Entry<RowIndexKey<Object[]>, ImmutableBitMap> entry : map.entrySet()) {
            List<Object> row = new ArrayList<Object>(Arrays.asList(entry.getKey().getKey()));
            for (int i = 0; i < metrics.size(); i++) {
                row.add(aggregators.get(i).aggregate(entry.getValue(), metrics.get(i)).calculate());
            }
            rows.add(new ListBasedRow(row));
        }
        return rows;
    }
}
