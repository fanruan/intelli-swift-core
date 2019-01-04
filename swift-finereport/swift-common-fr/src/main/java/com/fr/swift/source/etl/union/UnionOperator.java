package com.fr.swift.source.etl.union;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:45
 */
public class UnionOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(UnionOperator.class);
    //每个list表示一列数，第一个是合并之后的名字，往下依次是每个表的字段，没用到的为null
    @CoreField
    private List<List<String>> unionColumns;

    public UnionOperator(List<List<String>> unionColumns) {
        this.unionColumns = unionColumns;
    }

    public List<List<String>> getColumnNameList() {
        return this.unionColumns;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0; i < this.unionColumns.size(); i++) {
            List<String> list = this.unionColumns.get(i);
            ColumnTypeConstants.ClassType type = ColumnTypeConstants.ClassType.INTEGER;
            int columnSize = 0;
            int scale = 0;
            for (int j = 1; j < list.size(); j++) {
                try {
                    String columnName = list.get(j);
                    if (columnName == null) {
                        continue;
                    }
                    SwiftMetaDataColumn singleColumn = tables[j - 1].getColumn(list.get(j));
                    type = getMaxType(singleColumn, type);
                    columnSize = Math.max(columnSize, singleColumn.getPrecision());
                    scale = Math.max(scale, singleColumn.getScale());
                } catch (SwiftMetaDataException e) {
                    LOGGER.error("the field " + list.get(j) + " get meta failed", e);
                }
            }
            columnList.add(new MetaDataColumnBean(list.get(0), getSqlTypeByClassType(type), columnSize, scale));
        }
        return columnList;
    }

    private int getSqlTypeByClassType(ColumnTypeConstants.ClassType type) {
        switch (type) {
            case DATE:
                return Types.DATE;
            case INTEGER:
            case LONG:
                return Types.BIGINT;
            case DOUBLE:
                return Types.DOUBLE;
            default:
                return Types.VARCHAR;
        }
    }

    private ColumnTypeConstants.ClassType getMaxType(SwiftMetaDataColumn singleColumn, ColumnTypeConstants.ClassType type) {
        ColumnTypeConstants.ClassType columnClassType = ColumnTypeUtils.getClassType(singleColumn);
        if (columnClassType == ColumnTypeConstants.ClassType.STRING || columnClassType == ColumnTypeConstants.ClassType.DATE) {
            return columnClassType;
        }
        if (type.compareTo(columnClassType) > 0) {
            return type;
        } else {
            return columnClassType;
        }
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.UNION;
    }
}
