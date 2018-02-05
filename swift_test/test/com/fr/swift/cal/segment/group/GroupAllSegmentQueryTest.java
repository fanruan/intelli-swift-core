package com.fr.swift.cal.segment.group;

import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.multiby.CubeData;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.Iterator;
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

    public static void checkResult(RowResultCollector collector, Map<RowIndexKey, double[]> expectedResult) {
        Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator = collector.getRowResult().iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, AggregatorValue[]> keyValue = iterator.next();
            assertTrue(expectedResult.containsKey(keyValue.getKey()));
            double[] expectedValues = expectedResult.get(keyValue.getKey());
            AggregatorValue[] values = keyValue.getValue();
            assertEquals(expectedValues.length, values.length);
            for (int i = 0; i < values.length; i++) {
                assertEquals(expectedValues[i], values[i].calculate());
            }
        }
    }

    private void test(CubeData cubeData) {
        dimensions = cubeData.getDimensions();
        metrics = cubeData.getMetrics();
        expectedResult = cubeData.getAggregationResult();
        aggregators = cubeData.getAggregators();
        GroupAllSegmentQuery query = new GroupAllSegmentQuery(dimensions, metrics, aggregators, null);
        RowResultCollector collector = null;
        try {
            collector = query.getQueryResult();
        } catch (Exception e) {
            assertTrue(false);
        }
        checkResult(collector, expectedResult);
        // 测试字典map
        List<Map<Integer, Object>> dictionaries = collector.getGlobalDictionaries();
        assertEquals(dictionaries.size(), dimensions.size());
        for (int i = 0; i < dimensions.size(); i++) {
            assertEquals(dictionaries.get(i).size(), dimensions.get(i).getDictionaryEncodedColumn().size());
            DictionaryEncodedColumn dict = dimensions.get(i).getDictionaryEncodedColumn();
            for (Map.Entry entry : dictionaries.get(i).entrySet()) {
                assertEquals(entry.getKey(), dict.getGlobalIndexByIndex(dict.getIndex(entry.getValue())));
            }
        }
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
