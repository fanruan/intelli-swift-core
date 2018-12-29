package com.fr.swift.query.post;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.fr.swift.query.post.utils.ResultJoinUtils;

/**
 * Created by Lyon on 2018/6/4.
 */
public class PostQueryTestUtils {

//    public static GroupNode createNode(int dimensionSize, Pair<Object[], int[]>[] rows) {
//        List<Pair<Object[], int[]>> list = new ArrayList<Pair<Object[], int[]>>(Arrays.asList(rows));
//        Iterator<Pair<List<Object>, AggregatorValue[]>> rowIt = new MapperIterator<Pair<Object[], int[]>, Pair<List<Object>, AggregatorValue[]>>(list.iterator(), new Function<Pair<Object[], int[]>, Pair<List<Object>, AggregatorValue[]>>() {
//            @Override
//            public Pair<List<Object>, AggregatorValue[]> apply(Pair<Object[], int[]> p) {
//                AggregatorValue[] values = new AggregatorValue[p.getValue().length];
//                for (int i = 0; i < values.length; i++) {
//                    values[i] = new DoubleAmountAggregatorValue(p.getValue()[i]);
//                }
//                return Pair.of(Arrays.asList(p.getKey()), values);
//            }
//        });
//        return ResultJoinUtils.createNode(dimensionSize, rowIt);
//    }

    public static SwiftMetaData createMetaData(final String tableName, final String[] columnNames) {
        return new SwiftMetaData() {
            @Override
            public SwiftDatabase getSwiftDatabase() {
                return null;
            }

            @Override
            public String getSchemaName() {
                return null;
            }

            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int i) {
                return columnNames[i];
            }

            @Override
            public String getColumnRemark(int index) {
                return null;
            }

            @Override
            public int getColumnType(int index) {
                return 0;
            }

            @Override
            public int getPrecision(int index) {
                return 0;
            }

            @Override
            public int getScale(int index) {
                return 0;
            }

            @Override
            public SwiftMetaDataColumn getColumn(int index) {
                return null;
            }

            @Override
            public SwiftMetaDataColumn getColumn(String columnName) {
                return null;
            }

            @Override
            public int getColumnIndex(String columnName) {
                return 0;
            }

            @Override
            public String getColumnId(int index) {
                return null;
            }

            @Override
            public String getColumnId(String columnName) {
                return null;
            }

            @Override
            public String getRemark() {
                return null;
            }

            @Override
            public List<String> getFieldNames() {
                return new ArrayList<String>(Arrays.asList(columnNames));
            }

            @Override
            public String getId() {
                return null;
            }
        };
    }

    public static List<SwiftMetaData> createMetaData(String[] tableNames, String[][] columnNames) {
        List<SwiftMetaData> metaData = new ArrayList<SwiftMetaData>();
        for (int i = 0; i < 3; i++) {
            final int index = i;
            metaData.add(createMetaData(tableNames[index], columnNames[index]));
        }
        return metaData;
    }
}
