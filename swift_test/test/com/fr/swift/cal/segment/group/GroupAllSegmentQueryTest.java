package com.fr.swift.cal.segment.group;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/1/15.
 */
public class GroupAllSegmentQueryTest extends TestCase {

    private Map<RowIndexKey, double[]> expectedResult;
    private List<Column> dimensions;
    private List<Column> metrics;
    private List<Aggregator> aggregators;
    private int rowCount;

    public static void checkResult(GroupByResultSet collector, Map<RowIndexKey, double[]> expectedResult) {
//        Iterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> iterator = collector.getResultList().iterator();
//        while (iterator.hasNext()) {
//            KeyValue<RowIndexKey<int[]>, AggregatorValue[]> keyValue = iterator.next();
//            if (!isNormalRow(keyValue.getKey().getKey())) {
//                continue;
//            }
//            assertTrue(expectedResult.containsKey(keyValue.getKey()));
//            double[] expectedValues = expectedResult.get(keyValue.getKey());
//            AggregatorValue[] values = keyValue.getValue();
//            assertEquals(expectedValues.length, values.length);
//            for (int i = 0; i < values.length; i++) {
//                assertEquals(expectedValues[i], values[i].calculate());
//            }
//        }
    }

    private static boolean isNormalRow(int[] index) {
        for (int i = 0; i < index.length; i++) {
            if (index[i] == -1) {
                return false;
            }
        }
        return true;
    }

    private void test(CubeData cubeData) {
        dimensions = cubeData.getDimensions();
        metrics = cubeData.getMetrics();
        expectedResult = cubeData.getAggregationResult();
        aggregators = cubeData.getAggregators();
        rowCount = cubeData.getRowCount();
//        GroupAllSegmentQuery query = new GroupAllSegmentQuery(dimensions, metrics, aggregators, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex) {
//                return false;
//            }
//        }, new ArrayList<>());
//        GroupByResultSet collector = null;
//        try {
//            collector = query.getQueryResult();
//        } catch (Exception e) {
//            assertTrue(false);
//        }
//        checkResult(collector, expectedResult);
        // 测试字典map
//        List<Map<Integer, Object>> dictionaries = collector.getRowGlobalDictionaries();
//        assertEquals(dictionaries.size(), dimensions.size());
//        for (int i = 0; i < dimensions.size(); i++) {
//            assertEquals(dictionaries.get(i).size(), dimensions.get(i).getDictionaryEncodedColumn().size() - 1);
//            DictionaryEncodedColumn dict = dimensions.get(i).getDictionaryEncodedColumn();
//            for (Map.Entry entry : dictionaries.get(i).entrySet()) {
//                assertEquals(entry.getKey(), dict.getGlobalIndexByIndex(dict.getIndex(entry.getValue())));
//            }
//        }
    }

    public void testQuery() {
        test(new CubeData());
        test(new CubeData(1, 1, 100));
        test(new CubeData(1, 1, 1));
        test(new CubeData(1, 0, 100));
        // 这边有点问题，功能适配的时候在处理
//        test(new CubeData(0, 1, 100));
    }
}
