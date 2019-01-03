package com.fr.swift.segment;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SwiftMetaData;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/9
 */
public class DateResultSet extends SingleColumnResultSet {

    int count;

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new SwiftMetaDataBean("DATE_TABLE",
                Arrays.asList(new MetaDataColumnBean("date", Types.BIGINT)));
    }


    @Override
    protected void initData() {
        count = (int) (Math.random() * 1000000);
        for (int i = 0; i < count; i++) {
            List data = new ArrayList<Long>();
            data.add((long) (System.currentTimeMillis() + Math.random() * 1000000));
            datas.add(new ListBasedRow(data));
        }
        List data = new ArrayList<Long>();
        data.add(null);
        datas.add(new ListBasedRow(data));
    }
}
