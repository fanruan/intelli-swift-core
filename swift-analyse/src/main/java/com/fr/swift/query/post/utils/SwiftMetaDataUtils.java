package com.fr.swift.query.post.utils;

import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/1.
 */
public class SwiftMetaDataUtils {

    // TODO: 2018/6/1 先大概写一下，好多属性还不清楚怎么加进来
    public static SwiftMetaData createMetaData(GroupQueryInfo info) {
        final String tableName = info.getTable().getId();
        final List<String> columnNames = new ArrayList<String>();
        List<Dimension> dimensions = info.getDimensions();
        for (Dimension dimension : dimensions) {
            columnNames.add(dimension.getColumnKey().getName());
        }
        List<Metric> metrics = info.getMetrics();
        for (Metric metric : metrics) {
            columnNames.add(metric.getColumnKey().getName());
        }
        // TODO: 2018/6/1 结果计算中的新增列以及alias列名待处理
        return new SwiftMetaData() {
            @Override
            public SwiftDatabase.Schema getSwiftSchema() {
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
                return columnNames.size();
            }

            @Override
            public String getColumnName(int index) {
                return columnNames.get(index);
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
                return columnNames;
            }
        };
    }
}
