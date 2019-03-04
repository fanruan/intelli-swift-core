package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
abstract class BaseTable implements CSVTable {

    abstract String getTableName();

    abstract List<SwiftMetaDataColumn> getColumnList();

    @Override
    public SwiftMetaDataBean createBean(SwiftDatabase db) {
        SwiftMetaDataBean bean = new SwiftMetaDataBean();
        bean.setSwiftDatabase(db);
        bean.setId(getTableName());
        bean.setTableName(getTableName());
        bean.setFields(getColumnList());
        return bean;
    }
}
