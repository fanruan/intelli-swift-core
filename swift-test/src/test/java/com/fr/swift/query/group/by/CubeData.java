package com.fr.swift.query.group.by;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.IndexInfoImpl;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.test.Temps.TempDictColumn;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2018/1/15.
 */
public class CubeData {

    private int dimensionCount = 3;
    private int metricCount = 5;
    private int rowCount = 100;
    private static Random random = new Random(23435);
    private List<Column> dimensionColumns = new ArrayList<>();
    private List<Column> metricColumns = new ArrayList<>();
    private Map<RowIndexKey<int[]>, RowTraversal> bitMapGroup;
    private String[][] dimensions;
    private int[][] metrics;
    private Map<RowIndexKey<int[]>, double[]> aggregationResult;
    private List<Aggregator> aggregators;

    public CubeData() {
        init();
    }

    public CubeData(int dimensionCount, int metricCount, int rowCount) {
        this.dimensionCount = dimensionCount;
        this.metricCount = metricCount;
        this.rowCount = rowCount;
        init();
    }

    private void init() {
        prepareDetail();
        prepareIndex();
        prepareAggregationResult();
        prepareAggregators();
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<Pair<Column, IndexInfo>> getDimensions() {
        List<Pair<Column, IndexInfo>> pairs = new ArrayList<>();
        for (Column column : dimensionColumns) {
            pairs.add(Pair.of(column, new IndexInfoImpl(true, true)));
        }
        return pairs;
    }

    public List<Column> getMetrics() {
        return metricColumns;
    }

    public Map<RowIndexKey<int[]>, RowTraversal> getBitMapGroup() {
        return bitMapGroup;
    }

    public Map<RowIndexKey<int[]>, double[]> getAggregationResult() {
        return aggregationResult;
    }

    public List<Aggregator> getAggregators() {
        return aggregators;
    }

    private void prepareDetail() {
        String s = "abcdefg";
        List<String> alphabet = Arrays.asList(s.split(""));
        dimensions = new String[dimensionCount][rowCount];
        IntStream.range(0, dimensionCount).forEach(i -> IntStream.range(0, rowCount)
                .forEach(j -> dimensions[i][j] = alphabet.get(random.nextInt(1 + (i + 3) % alphabet.size()))));
        metrics = new int[metricCount][rowCount];
        IntStream.range(0, metricCount).forEach(i -> IntStream.range(0, rowCount)
                .forEach(j -> metrics[i][j] = random.nextInt(100)));
    }

    private void prepareIndex() {
        // 创建每一列的分组索引
        List<TreeMap<String, List<Integer>>> dict = new ArrayList<>();
        IntStream.range(0, dimensionCount).forEach(i -> {
            dict.add(new TreeMap<>((a, b) -> {
                if (a == null && b == null) {
                    return 0;
                }
                if (a == null) {
                    return 1;
                }
                if (b == null) {
                    return -1;
                }
                return Comparator.<String>naturalOrder().compare(a, b);
            }));
            dict.get(i).put("NULL", new ArrayList<>());
        });
        IntStream.range(0, dimensionCount).forEach(i -> IntStream.range(0, rowCount).forEach(j -> {
            Map<String, List<Integer>> d = dict.get(i);
            if (!d.containsKey(dimensions[i][j])) {
                d.put(dimensions[i][j], new ArrayList<>());
            }
            d.get(dimensions[i][j]).add(j);
        }));

        // 创建维度column
        for (int i = 0; i < dimensionCount; i++) {
            final int columnIndex = i;
            dimensionColumns.add(new Column() {
                @Override
                public IResourceLocation getLocation() {
                    return null;
                }

                @Override
                public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                    return new TempDictColumn() {

                        private Map<Integer, Integer> globalIndexMap = new HashMap<>();

                        @Override
                        public int size() {
                            return dict.get(columnIndex).size();
                        }

                        @Override
                        public void putGlobalIndex(int index, int globalIndex) {
                            globalIndexMap.put(index, globalIndex);
                        }

                        @Override
                        public Object getValue(int index) {
                            return new ArrayList<>(dict.get(columnIndex).keySet()).get(index);
                        }

                        @Override
                        public int getIndex(Object value) {
                            return new ArrayList<>(dict.get(columnIndex).keySet()).indexOf(value);
                        }

                        @Override
                        public int getIndexByRow(int row) {
                            List<String> dictionary = new ArrayList<>(dict.get(columnIndex).keySet());
                            return dictionary.indexOf(dimensions[columnIndex][row]);
                        }

                        @Override
                        public int getGlobalIndexByIndex(int index) {
                            return globalIndexMap.isEmpty() ? index : globalIndexMap.get(index);
                        }

                        @Override
                        public Comparator getComparator() {
                            return Comparator.<String>naturalOrder();
                        }
                    };
                }

                @Override
                public BitmapIndexedColumn getBitmapIndex() {
                    return new BitmapIndexedColumn() {
                        @Override
                        public void flush() {

                        }

                        @Override
                        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

                        }

                        @Override
                        public ImmutableBitMap getBitMapIndex(int index) {
                            IntList intList = IntListFactory.createIntList();
                            new ArrayList<>(dict.get(columnIndex).values()).get(index)
                                    .stream().forEach(i -> intList.add(i));
                            return BitMaps.newImmutableBitMap(intList);
                        }

                        @Override
                        public void putNullIndex(ImmutableBitMap bitMap) {

                        }

                        @Override
                        public ImmutableBitMap getNullIndex() {
                            return null;
                        }

                        @Override
                        public void release() {

                        }
                    };
                }

                @Override
                public DetailColumn getDetailColumn() {
                    return null;
                }
            });
        }

        // 创建指标column
        for (int i = 0; i < metricCount; i++) {
            final int columnIndex = i;
            metricColumns.add(new Column() {
                @Override
                public IResourceLocation getLocation() {
                    return null;
                }

                @Override
                public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                    return new TempDictColumn() {
                        @Override
                        public ColumnTypeConstants.ClassType getType() {
                            return ColumnTypeConstants.ClassType.INTEGER;
                        }
                    };
                }

                @Override
                public BitmapIndexedColumn getBitmapIndex() {
                    return new BitmapIndexedColumn() {
                        @Override
                        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

                        }

                        @Override
                        public ImmutableBitMap getBitMapIndex(int index) {
                            return null;
                        }

                        @Override
                        public void putNullIndex(ImmutableBitMap bitMap) {

                        }

                        @Override
                        public ImmutableBitMap getNullIndex() {
                            return null;
                        }

                        @Override
                        public void flush() {

                        }

                        @Override
                        public void release() {

                        }
                    };
                }

                @Override
                public DetailColumn getDetailColumn() {
                    DetailColumn column = EasyMock.createMock(DetailColumn.class);
                    for (int i = 0; i < rowCount; i++) {
                        EasyMock.expect(column.getInt(i)).andReturn(metrics[columnIndex][i]).times(1);
                    }
                    EasyMock.replay(column);
                    return column;
                }
            });
        }

        // group by
        List<List<String>> groupValues = new ArrayList<>();
        for (int i = 0; i < dict.size(); i++) {
            groupValues.add(new ArrayList<>(dict.get(i).keySet()));
        }
        Map<RowIndexKey, IntList> map = new LinkedHashMap<>();
        IntStream.range(0, rowCount).forEach(i -> {
            int[] indexes = new int[dimensionCount];
            for (int n = 0; n < dimensionCount; n++) {
                indexes[n] = groupValues.get(n).indexOf(dimensions[n][i]);
            }
            RowIndexKey key = new RowIndexKey(indexes);
            if (!map.containsKey(key)) {
                map.put(key, IntListFactory.createIntList());
            }
            map.get(key).add(i);
        });
        bitMapGroup = new LinkedHashMap<>();
        for (Map.Entry<RowIndexKey, IntList> entry : map.entrySet()) {
            bitMapGroup.put(entry.getKey(), new IntListRowTraversal(entry.getValue()));
        }
    }

    private void prepareAggregationResult() {
        aggregationResult = new LinkedHashMap<>();
        for (RowIndexKey key : bitMapGroup.keySet()) {
            RowTraversal traversal = bitMapGroup.get(key);
            double[] sum = new double[metricCount];
            Arrays.fill(sum, 0);
            traversal.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    for (int i = 0; i < metricCount; i++) {
                        sum[i] += metrics[i][row];
                    }
                }
            });
            aggregationResult.put(key, sum);
        }
    }

    private void prepareAggregators() {
        aggregators = new ArrayList<>();
        for (int i = 0; i < metricColumns.size(); i++) {
            aggregators.add(new SumAggregate());
        }
    }

    public static void prepareGlobalIndex(CubeData... cubes) {
        // 前提：所有cubes的维度个数相同
        int dimensionSize = cubes[0].getDimensions().size();
        List<TreeSet<String>> dictionaries = new ArrayList<>();
        for (int i = 0; i < dimensionSize; i++) {
            dictionaries.add(new TreeSet<>(Comparator.naturalOrder()));
        }
        for (CubeData cubeData : cubes) {
            List<Pair<Column, IndexInfo>> columns = cubeData.getDimensions();
            for (int i = 0; i < columns.size(); i++) {
                DictionaryEncodedColumn dict = columns.get(i).getKey().getDictionaryEncodedColumn();
                for (int j = 0; j < dict.size(); j++) {
                    dictionaries.get(i).add((String) dict.getValue(j));
                }
            }
        }
        List<List<String>> lists = new ArrayList<>();
        for (TreeSet<String> set : dictionaries) {
            lists.add(new ArrayList<>(set));
        }
        for (CubeData cubeData : cubes) {
            List<Pair<Column, IndexInfo>> columns = cubeData.getDimensions();
            for (int i = 0; i < columns.size(); i++) {
                DictionaryEncodedColumn dict = columns.get(i).getKey().getDictionaryEncodedColumn();
                List<String> globalMap = lists.get(i);
                for (int j = 0; j < dict.size(); j++) {
                    dict.putGlobalIndex(j, globalMap.indexOf(dict.getValue(j)));
                }
            }
        }
    }
}
