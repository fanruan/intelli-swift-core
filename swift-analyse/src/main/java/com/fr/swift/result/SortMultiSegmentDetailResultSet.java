package com.fr.swift.result;

import com.fr.swift.query.query.Query;
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

    /**
     * 类似纸牌游戏，n块数据时unsortedRows数组大小为n;每次分别从n块排好序的数据中取一行数据放在unsortedRows对应的位置上,
     * 比较出这n行数据的最大或最小值frontRow，并且从该frontRow所在的块取下一行数据放在unsortedRows对应的位置上（替换掉
     * 该位置上的值）;数组元素为空表示该数组位置对应的块数据已经被取完。
     */
    private Row getLatestRowData(Row frontRow) throws SQLException {
        int pos = 0;
        for (int i = 1; i < unsortedRows.length; i++) {
            while (unsortedRows[i] == null) {
                if (i >= unsortedRows.length - 1) {
                    break;
                }
                i = i + 1;
            }
            if (unsortedRows[i] == null) {
                continue;
            }
            if (frontRow == null) {
                frontRow = unsortedRows[i];
                pos = i;
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
