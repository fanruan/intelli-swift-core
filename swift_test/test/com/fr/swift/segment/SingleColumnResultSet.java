package com.fr.swift.segment;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/9
 */
public abstract class SingleColumnResultSet implements SwiftResultSet {
    protected List<Row> datas = new ArrayList<Row>();
    int position = 0;

    public SingleColumnResultSet() {
        initData();
    }

    @Override
    public void close() throws SQLException {

    }

    protected abstract void initData();

    @Override
    public boolean next() throws SQLException {
        return position < datas.size();
    }

    @Override
    public Row getRowData() throws SQLException {
        return datas.get(position++);
    }
}
