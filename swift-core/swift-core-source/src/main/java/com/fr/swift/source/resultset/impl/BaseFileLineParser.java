package com.fr.swift.source.resultset.impl;

import com.fr.general.DateUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.SwiftStreamResultSet;
import com.fr.swift.util.Crasher;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseFileLineParser implements SwiftStreamResultSet.LineParser {

    private static final String NUMBER_REG = "^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$";
    private List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
    private Row firstRow;

    @Override
    public void setColumns(List<SwiftMetaDataColumn> columns) {
        this.columns = columns;
    }

    @Override
    public Row parseLine(String line) {
        Row row = split(line);
        if (null == columns || columns.isEmpty()) {
            return row;
        }
        List data = new ArrayList();
        if (row.getSize() != columns.size()) {
            Crasher.crash(String.format("Parser expect column size %d but get %d", columns.size(), row.getSize()));
        }
        for (int i = 0; i < columns.size(); i++) {
            String col = row.getValue(i);
            try {
                switch (columns.get(i).getType()) {
                    case Types.DOUBLE:
                        data.add(Double.parseDouble(col));
                        break;
                    case Types.VARCHAR:
                        data.add(col);
                        break;
                    case Types.DATE:
                        data.add(Long.parseLong(col));
                        break;
                    default:
                        data.add(null);
                        break;
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn(e);
                data.add(null);
            }
        }
        return new ListBasedRow(data);
    }

    @Override
    public List<SwiftMetaDataColumn> parseColumns(String head, String firstRow) {
        if (null != columns) {
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
            if (StringUtils.isEmpty(firstRow)) {
                col = columnNameSplit.getValue(i);
            } else {
                col = typeSplit.getValue(i);
            }
            boolean isDate = false;
            try {
                Date date = DateUtils.string2Date(col, true);
                if (null != date) {
                    isDate = true;
                }
            } catch (Exception ignore) {
            }

            // 如果为null可以防止空指针
            if (StringUtils.isEmpty(col)) {
                col = StringUtils.EMPTY;
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
        if (!isSkipFirstLine()) {
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
