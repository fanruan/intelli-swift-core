package com.fr.swift.source.etl.columnrowtrans;

import com.fr.general.ComparatorUtils;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/23 0023 14:14
 */
public class ColumnRowTransOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnRowTransOperator.class);
    private String groupName;
    private String lcName;
    private List<NameText> lcValue;
    private List<NameText> columns;
    private List<String> otherColumnNames;

    public ColumnRowTransOperator(String groupName, String lcName, List<NameText> lcValue, List<NameText> columns, List<String> otherColumnNames) {
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

    public List<NameText> getLcValue() {
        return this.lcValue;
    }

    public List<NameText> getColumns() {
        return this.columns;
    }

    public List<String> getOtherColumnNames() {
        return this.otherColumnNames;
    }

    public String getNewAddedName() {
        return null;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        try {
            for(SwiftMetaData table : tables) {
                SwiftMetaDataColumn singleColumn = table.getColumn(groupName);
                columnList.add(new MetaDataColumn(groupName, groupName, singleColumn.getType(), singleColumn.getPrecision(), singleColumn.getScale()));
                for(int i = 0; i < table.getColumnCount(); i++) {
                    SwiftMetaDataColumn tarColumn = table.getColumn(table.getColumnName(i + 1));
                    if(!isColumnSelected(tarColumn.getName())) {
                        String tarColumnName = tarColumn.getName();
                        columnList.add(new MetaDataColumn(tarColumnName, null, tarColumn.getType(), tarColumn.getPrecision(), tarColumn.getScale()));
                    }
                }
                for(NameText column : this.columns) {
                    SwiftMetaDataColumn c = table.getColumn(column.origin);
                    for (NameText aLcValue : lcValue) {
                        String lcColumn = aLcValue.origin + "-" + column.origin;
                        String text = aLcValue.getTransText() + "-" + column.getTransText();
                        String lcColumnText = ComparatorUtils.equals(text, lcColumn) ? null : text;
                        columnList.add(new MetaDataColumn(lcColumn, lcColumnText, c.getType(), c.getPrecision(), c.getScale()));
                    }
                }
            }
        } catch(SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.COLUMN_ROW_TRANS;
    }

    private boolean isColumnSelected(String name) {
        if (ComparatorUtils.equals(name, this.lcName)) {
            return true;
        }
        if (ComparatorUtils.equals(name, this.groupName)) {
            return true;
        }
        for (NameText nt : this.columns) {
            if (ComparatorUtils.equals(nt.origin, name)) {
                return true;
            }
        }
        return false;
    }
}
