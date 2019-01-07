package com.fr.swift.query.group.by;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.impl.BaseColumnImplTest;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.IndexInfoImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.test.Temps;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Lyon on 2018/1/15.
 */
public class CubeData {

    private int dimensionCount = 3;
    private int metricCount = 5;
    private int rowCount = 1000;
    private List<Column> dimensionColumns = new ArrayList<Column>();
    private List<Column> metricColumns = new ArrayList<Column>();

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
        prepareColumn();
    }

    public List<Pair<Column, IndexInfo>> getDimensions() {
        List<Pair<Column, IndexInfo>> pairs = new ArrayList<Pair<Column, IndexInfo>>();
        for (Column column : dimensionColumns) {
            pairs.add(Pair.<Column, IndexInfo>of(column, new IndexInfoImpl(true, false)));
        }
        return pairs;
    }

    public List<Column> getMetrics() {
        return metricColumns;
    }

    private void prepareColumn() {
        for (int i = 0; i < dimensionCount; i++) {
            dimensionColumns.add(new BaseColumnImplTest<String>(BaseFilterTest.createStrDetail(rowCount), Comparators.STRING_ASC));
        }
        for (int i = 0; i < metricCount; i++) {
            final List<Integer> detail = BaseFilterTest.intDetails(rowCount);
            metricColumns.add(new BaseColumnImplTest<Integer>(detail, Comparators.<Integer>asc()) {
                @Override
                public DetailColumn getDetailColumn() {
                    final DetailColumn column = super.getDetailColumn();
                    return new Temps.TempDetailColumn() {
                        @Override
                        public Object get(int pos) {
                            return column.get(pos);
                        }

                        @Override
                        public int getInt(int pos) {
                            return detail.get(pos);
                        }
                    };
                }
            });
        }
    }

    public static void prepareGlobalIndex(CubeData... cubes) {
        // 前提：所有cubes的维度个数相同
        int dimensionSize = cubes[0].getDimensions().size();
        List<TreeSet<String>> dictionaries = new ArrayList<TreeSet<String>>();
        for (int i = 0; i < dimensionSize; i++) {
            dictionaries.add(new TreeSet<String>(Comparators.STRING_ASC));
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
        List<List<String>> lists = new ArrayList<List<String>>();
        for (TreeSet<String> set : dictionaries) {
            lists.add(new ArrayList<String>(set));
        }
        for (CubeData cubeData : cubes) {
            List<Pair<Column, IndexInfo>> columns = cubeData.getDimensions();
            for (int i = 0; i < columns.size(); i++) {
                DictionaryEncodedColumn dict = columns.get(i).getKey().getDictionaryEncodedColumn();
                List<String> globalMap = lists.get(i);
                for (int j = 0; j < dict.size(); j++) {
                    dict.putter().putGlobalIndex(j, globalMap.indexOf(dict.getValue(j)));
                }
            }
        }
    }
}
