package com.fr.swift.segment;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/9
 */
public class DoubleResultSet extends SingleColumnResultSet {

    int count;

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new SwiftMetaDataBean("DOUBLE_TABLE",
                Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnBean("double", Types.DOUBLE)));
    }


    @Override
    protected void initData() {
        count = (int) (Math.random() * 1000000);
        for (int i = 0; i < count; i++) {
            List data = new ArrayList<Double>();
            data.add(Math.random() * count);
            datas.add(new ListBasedRow(data));
        }
        List data = new ArrayList<Double>();
        data.add(null);
        datas.add(new ListBasedRow(data));
    }
}
