package com.fr.swift.result;

import com.fr.swift.cal.Query;
import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/23
 */

public class MultiSegmentDetailResultSet extends DetailResultSet {

    /**
     * 第几块
     */
    private int index = 0;
    private List<DetailResultSet> drs = new ArrayList<DetailResultSet>();
    private List<Query<DetailResultSet>> queries;

    public MultiSegmentDetailResultSet(List<Query<DetailResultSet>> queries) {
        this.queries = queries;
        init();
    }

    @Override
    public void close() throws SQLException {

    }


    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {

        while (!(drs.get(index).next())) {
            index ++;
        }
        return drs.get(index).getRowData();
    }


    private void init() {
        try {
            for (Query query : queries) {
                maxRow += ((SegmentDetailResultSet) query.getQueryResult()).getMaxRow();
                drs.add((SegmentDetailResultSet) query.getQueryResult());
            }
        } catch (SQLException e) {

        }
    }
}
