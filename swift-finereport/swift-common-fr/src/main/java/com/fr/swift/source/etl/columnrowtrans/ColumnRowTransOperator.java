package com.fr.swift.source.etl.columnrowtrans;

import com.fr.general.ComparatorUtils;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/23 0023 14:14
 */
public class ColumnRowTransOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnRowTransOperator.class);
    /**
     * 分组名
     */
    @CoreField
    private String groupName;
    /**
     * 栏次字段名
     */
    @CoreField
    private String lcName;
    /**
     * 栏次值
     */
    @CoreField
    private List<Pair<String, String>> lcValue;
    /**
     * 行列转化的指标名称
     */
    @CoreField
    private List<Pair<String, String>> columns;
    /**
     * 其他指标名
     */
    @CoreField
    private List<Pair<String, String>> otherColumnNames;

    public ColumnRowTransOperator(String groupName, String lcName, List<Pair<String, String>> lcValue, List<Pair<String, String>> columns, List<Pair<String, String>> otherColumnNames) {
        this.groupName = groupName;
        this.lcName = lcName;
        this.lcValue = lcValue;
        this.columns = columns;
        this.otherColumnNames = otherColumnNames;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getLcName() {
        return this.lcName;
    }

    public List<Pair<String, String>> getLcValue() {
        return this.lcValue;
    }

    public List<Pair<String, String>> getColumns() {
        return this.columns;
    }

    public List<Pair<String, String>> getOtherColumnNames() {
        return this.otherColumnNames;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        try {
            SwiftMetaData table = tables[0];
            SwiftMetaDataColumn groupColumn = table.getColumn(groupName);
            columnList.add(new MetaDataColumnBean(groupName, groupName, groupColumn.getType(), groupColumn.getPrecision(), groupColumn.getScale()));
            for (Pair<String, String> column : this.columns) {
                SwiftMetaDataColumn c = table.getColumn(column.getKey());
                for (Pair<String, String> aLcValue : lcValue) {
                    String lcColumn = aLcValue.getKey() + "-" + column.getKey();
                    String text = aLcValue.getValue() + "-" + column.getValue();
                    String lcColumnText = ComparatorUtils.equals(text, lcColumn) ? null : text;
                    columnList.add(new MetaDataColumnBean(lcColumn, lcColumnText, c.getType(), c.getPrecision(), c.getScale()));
                }
            }
            for (Pair<String, String> column : this.otherColumnNames) {
                SwiftMetaDataColumn c = table.getColumn(column.getKey());
                columnList.add(new MetaDataColumnBean(column.getKey(), column.getValue(), c.getType(), c.getPrecision(), c.getScale()));
            }
        } catch (SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.COLUMN_ROW_TRANS;
    }
}
