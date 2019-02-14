package com.fr.swift.source.resultset.importing.file.impl;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.importing.file.FileLineParser;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.DateUtils;
import com.fr.swift.util.Strings;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseFileLineParser implements FileLineParser {

    private static final String NUMBER_REG = "^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$";
    private List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
    private Row firstRow;
    private LineParserAdaptor adaptor;

    public BaseFileLineParser(LineParserAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public BaseFileLineParser() {
    }

    @Override
    public void setColumns(List<SwiftMetaDataColumn> columns) {
        this.columns = columns;
    }

    public static void addRowDataFromString(List data, String col, ColumnTypeConstants.ClassType classType) {
        switch (classType) {
            case DOUBLE:
                data.add(Double.parseDouble(col));
                break;
            case INTEGER:
            case LONG:
                data.add(Long.parseLong(col));
                break;
            case DATE:
                data.add(DateUtils.string2Date(col).getTime());
                break;
            default:
                data.add(col.trim());
                break;
        }
    }

    @Override
    public Row parseLine(String line) {
        Row row = split(line);
        if (null == columns || columns.isEmpty()) {
            if (null != adaptor) {
                return adaptor.adapt(row);
            }
            return row;
        }
        if (null != adaptor) {
            row = adaptor.adapt(row);
        }
        List data = new ArrayList();
        if (row.getSize() > columns.size()) {
            Crasher.crash(String.format("Parser expect column size %d but get %d", columns.size(), row.getSize()));
        }
        for (int i = 0; i < row.getSize(); i++) {
            String col = row.getValue(i);
            try {
                ColumnTypeConstants.ClassType classType = ColumnTypeUtils.getClassType(columns.get(i));
                addRowDataFromString(data, col, classType);
            } catch (Exception e) {
//                SwiftLoggers.getLogger().warn(e.getMessage());
                data.add(null);
            }
        }
        return new ListBasedRow(data);

    }

    @Override
    public List<SwiftMetaDataColumn> parseColumns(String head, String firstRow) {
        if (null != columns && !columns.isEmpty()) {
            return columns;
        }
        Row columnNameSplit = split(head);
        Row typeSplit = null;
        if (head.equals(firstRow)) {
            typeSplit = columnNameSplit;
        } else {
            typeSplit = split(firstRow);
        }
        columns = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0; i < columnNameSplit.getSize(); i++) {
            String col;
            if (Strings.isEmpty(firstRow)) {
                col = columnNameSplit.getValue(i);
            } else {
                col = typeSplit.getValue(i);
            }
            boolean isDate = false;
            try {
                Date date = DateUtils.string2Date(col);
                if (null != date) {
                    isDate = true;
                }
            } catch (Exception ignore) {
            }

            // 如果为null可以防止空指针
            if (Strings.isEmpty(col)) {
                col = Strings.EMPTY;
            }
            if (isSkipFirstLine()) {
                if (col.matches(NUMBER_REG)) {
                    columns.add(new MetaDataColumnBean(columnNameSplit.getValue(i).toString(), Types.DOUBLE));
                } else if (isDate) {
                    columns.add(new MetaDataColumnBean(columnNameSplit.getValue(i).toString(), Types.DATE));
                } else {
                    columns.add(new MetaDataColumnBean(columnNameSplit.getValue(i).toString(), Types.VARCHAR));
                }
            } else {
                if (col.matches(NUMBER_REG)) {
                    columns.add(new MetaDataColumnBean(String.format("Number%d", i), Types.DOUBLE));
                } else if (isDate) {
                    columns.add(new MetaDataColumnBean(String.format("Date%d", i), Types.DATE));
                } else {
                    columns.add(new MetaDataColumnBean(String.format("Varchar%d", i), Types.VARCHAR));
                }
            }
        }
        if (head.equals(firstRow) && !isSkipFirstLine()) {
            this.firstRow = parseLine(firstRow);
        } else if (!head.equals(firstRow) && Strings.isNotEmpty(firstRow)) {
            this.firstRow = parseLine(firstRow);
        }
        return columns;
    }

    @Override
    public Row firstRow() {
        return firstRow;
    }

    protected abstract Row split(String line);
}
