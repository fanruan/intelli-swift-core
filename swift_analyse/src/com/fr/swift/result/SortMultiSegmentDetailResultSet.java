package com.fr.swift.result;

import com.fr.swift.cal.Query;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 */

public class SortMultiSegmentDetailResultSet extends DetailResultSet {
    private List<Query<DetailResultSet>> queries;
    private Comparator comparator;
    private Row[] unsortedRows;
    private SwiftResultSet[] rs;
    private SwiftMetaData metaData;

    public SortMultiSegmentDetailResultSet(List<Query<DetailResultSet>> queries, Comparator comparator, SwiftMetaData metaData) throws SQLException {
        this.unsortedRows = new Row[queries.size()];
        this.rs = new SwiftResultSet[queries.size()];
        this.queries = queries;
        this.comparator = comparator;
        this.metaData = metaData;
        init();
    }


    @Override
    public Row getRowData() throws SQLException {
        return getLatestRowData(unsortedRows[0]);
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    private void init() throws SQLException {
        int i = 0;
        for (Query query : queries) {
            rs[i] = query.getQueryResult();
            if (rs[i].next()) {
                unsortedRows[i] = rs[i].getRowData();
            }
            maxRow += ((DetailResultSet) query.getQueryResult()).getRowSize();
            i++;
        }

    }

    private Row getLatestRowData(Row frontRow) throws SQLException {
        int pos = 0;
        for (int i = 1; i < unsortedRows.length; i++) {
            while (unsortedRows[i] == null) {
                i = i + 1;
            }
            if (frontRow == null) {
                frontRow = unsortedRows[i];
            }
            if (comparator.compare(frontRow, unsortedRows[i]) > 0) {
                frontRow = unsortedRows[i];
                pos = i;
            }
        }
        if (rs[pos].next()) {
            unsortedRows[pos] = rs[pos].getRowData();
        } else {
            unsortedRows[pos] = null;
        }
        return frontRow;
    }
}
