package com.fr.swift.source.etl.sort;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018-1-31 19:22:51
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnSortOperatorResultSet implements SwiftResultSet {

    private List<List<Object>> dataLists;
    private int rowNumber;
    private SwiftMetaData metaData;

    public ColumnSortOperatorResultSet(List<List<Object>> dataLists, SwiftMetaData metaData) {
        this.dataLists = dataLists;
        this.metaData = metaData;
        this.rowNumber = -1;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        if (dataLists.size() == 0) {
            return false;
        } else {
            return rowNumber++ < dataLists.size() - 1;
        }
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() {
        return new ListBasedRow(dataLists.get(rowNumber));
    }
}
