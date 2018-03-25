package com.fr.swift.result;

import com.fr.swift.cal.Query;
import com.fr.swift.exception.meta.SwiftMetaDataException;
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

    public MultiSegmentDetailResultSet(List<Query<DetailResultSet>> queries) throws SQLException {
        this.queries = queries;
        init();
    }

    @Override
    public Row getRowData() throws SQLException {
        DetailResultSet rs = drs.get(index);
        while (!rs.next()) {
            index++;
        }
        return rs.getRowData();
    }

    private void init() throws SQLException{
            for (Query query : queries) {
                maxRow += ((SegmentDetailResultSet) query.getQueryResult()).getMaxRow();
                drs.add((SegmentDetailResultSet) query.getQueryResult());
            }
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new DetailMetaData(){
            @Override
            public int getColumnCount() throws SwiftMetaDataException {
                DetailResultSet drs = null;
                try {
                    drs = queries.get(0).getQueryResult();
                } catch (SQLException e) {

                }
                return drs.getColumnCount();
            }
        };
    }
}
