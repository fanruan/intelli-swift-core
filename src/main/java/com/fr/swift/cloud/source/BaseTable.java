package com.fr.swift.cloud.source;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.table.CloudTable;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public abstract class BaseTable implements CloudTable {

    protected List<SwiftMetaDataColumn> rawColumns = new ArrayList<SwiftMetaDataColumn>();

    public List<SwiftMetaDataColumn> getRawColumns() {
        return rawColumns;
    }

    public List<SwiftMetaDataColumn> getExtraColumns() {
        return new ArrayList<SwiftMetaDataColumn>();
    }

    @Override
    public SwiftMetaDataBean createBean(SwiftDatabase db) {
        SwiftMetaDataBean bean = new SwiftMetaDataBean();
        bean.setSwiftDatabase(db);
        bean.setId(getTableName());
        bean.setTableName(getTableName());
        LineParser parser = getParser();
        bean.setFields(parser.getFields());
        return bean;
    }

    public abstract LineParser getParser();
}

