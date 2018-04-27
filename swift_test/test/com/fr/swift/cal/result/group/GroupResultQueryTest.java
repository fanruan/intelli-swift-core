package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.segment.group.GroupAllSegmentQuery;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2018/1/24.
 */
public class GroupResultQueryTest extends TestCase {

    private GroupByResultSet collector;
    private Map<RowIndexKey, double[]> expectedResult;
    private List<List<String>> expectedDictionaries;

    public GroupResultQueryTest() {
    }

    public GroupResultQueryTest(int segmentCount, int dimensionCount, int metricCount, int rowCount) {
        prepareGroupResultQuery(segmentCount, dimensionCount, metricCount, rowCount);
    }

    public GroupByResultSet getCollector() {
        return collector;
    }

    public Map<RowIndexKey, double[]> getExpectedResult() {
        return expectedResult;
    }

    public List<List<String>> getExpectedDictionaries() {
        return expectedDictionaries;
    }

    public void testQueryWithOnlyOneSegment() {
//        prepareGroupResultQuery(1, 3, 2, 100);
//        GroupAllSegmentQueryTest.checkResult(collector, expectedResult);
//        checkDictionaryMerging(collector.getRowGlobalDictionaries());
    }

    public void testQuery() {
//        prepareGroupResultQuery(3, 3, 2, 100);
//        GroupAllSegmentQueryTest.checkResult(collector, expectedResult);
//        checkDictionaryMerging(collector.getRowGlobalDictionaries());
    }

    public void checkDictionaryMerging(List<Map<Integer, Object>> actualDictionaries) {
        assertEquals(actualDictionaries.size(), expectedDictionaries.size());
        for (int i = 0; i < actualDictionaries.size(); i++) {
            for (Map.Entry<Integer, Object> entry : actualDictionaries.get(i).entrySet()) {
                assertEquals(entry.getKey(), (Integer) expectedDictionaries.get(i).indexOf(entry.getValue()));
            }
        }
    }

    private void prepareGroupResultQuery(int segmentCount, int dimensionCount, int metricCount, int rowCount) {
        List<Query<NodeResultSet>> queryList = new ArrayList<>();
        List<Aggregator> aggregators = new ArrayList<>();
        List<Sort> indexSorts = new ArrayList<>();
        List<Map<RowIndexKey, double[]>> expectedResultList = new ArrayList<>();
        List<List<Column>> dimensions = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            CubeData cubeData = new CubeData(dimensionCount, metricCount, rowCount);
            GroupAllSegmentQuery query = new GroupAllSegmentQuery(null, null);
            queryList.add(query);
            aggregators = cubeData.getAggregators();
            expectedResultList.add(cubeData.getAggregationResult());
            dimensions.add(cubeData.getDimensions());
        }
        for (int i = 0; i < dimensionCount; i++) {
            indexSorts.add(new AscSort(i));
        }
        expectedDictionaries = getGlobalDictionaries(dimensions);
        expectedResult = mergeResult(expectedResultList);
        updateGlobalIndex(dimensions, expectedDictionaries);
        GroupResultQuery groupResultQuery = new GroupResultQuery(queryList, aggregators, null);
//        try {
//            collector = groupResultQuery.getQueryResult();
//        } catch (SQLException e) {
//            assertTrue(false);
//        }
    }

    private void updateGlobalIndex(List<List<Column>> dimensions, List<List<String>> globalDictionaries) {
        for (int segmentIndex = 0; segmentIndex < dimensions.size(); segmentIndex++) {
            for (int dimensionIndex = 0; dimensionIndex < dimensions.get(segmentIndex).size(); dimensionIndex++) {
                DictionaryEncodedColumn column = dimensions.get(segmentIndex).get(dimensionIndex).getDictionaryEncodedColumn();
                List<String> dict = globalDictionaries.get(dimensionIndex);
                for (int i = 0; i < column.size(); i++) {
                    column.putGlobalIndex(i, dict.indexOf(column.getValue(i)));
                }
            }
        }
    }

    private List<List<String>> getGlobalDictionaries(List<List<Column>> dimensions) {
        List<TreeSet<String>> dict = new ArrayList<>();
        IntStream.range(0, dimensions.get(0).size()).forEach(i -> dict.add(new TreeSet<>(Comparator.naturalOrder())));
        for (int segmentIndex = 0; segmentIndex < dimensions.size(); segmentIndex++) {
            for (int dimensionIndex = 0; dimensionIndex < dimensions.get(segmentIndex).size(); dimensionIndex++) {
                DictionaryEncodedColumn dictionaryEncodedColumn = dimensions.get(segmentIndex).get(dimensionIndex).getDictionaryEncodedColumn();
                for (int n = 0; n < dictionaryEncodedColumn.size(); n++) {
                    dict.get(dimensionIndex).add((String) dictionaryEncodedColumn.getValue(n));
                }
            }
        }
        List<List<String>> result = new ArrayList<>();
        IntStream.range(0, dict.size()).forEach(i -> result.add(new ArrayList<>(dict.get(i))));
        return result;
    }

    private static Map<RowIndexKey, double[]> mergeResult(List<Map<RowIndexKey, double[]>> expectedResultList) {
        Map<RowIndexKey, double[]> result = new HashMap<>();
        for (Map<RowIndexKey, double[]> map : expectedResultList) {
            for (RowIndexKey key : map.keySet()) {
                double[] values = map.get(key);
                if (!result.containsKey(key)) {
                    double[] sum = new double[values.length];
                    Arrays.fill(sum, 0);
                    result.put(key, sum);
                }
                for (int i = 0; i < values.length; i++) {
                    result.get(key)[i] += values[i];
                }
            }
        }
        return result;
    }
}
