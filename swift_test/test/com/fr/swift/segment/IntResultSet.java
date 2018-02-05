package com.fr.swift.segment;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/9
 */
public class IntResultSet extends SingleColumnResultSet {

    int count;

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return new SwiftMetaDataImpl("INT_TABLE",
                Arrays.asList(new MetaDataColumn("int", Types.INTEGER)));
    }


    @Override
    protected void initData() {
        count = (int) (Math.random() * 1000000);
        for (int i = 0; i < count; i++) {
            List data = new ArrayList<Integer>();
            data.add(i);
            datas.add(new ListBasedRow(data));
        }
        List data = new ArrayList<Integer>();
        data.add(null);
        datas.add(new ListBasedRow(data));
    }
}
