package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.load.AddColumnAdapter;
import com.fr.swift.cloud.source.load.GeneralLineParser;
import com.fr.swift.cloud.source.load.LineAdapter;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.load.RawCSVParser;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
abstract class BaseTable implements CSVTable {

    private String appId;
    private String yearMonth;

    public BaseTable(String appId, String yearMonth) {
        this.appId = appId;
        this.yearMonth = yearMonth;
    }

    abstract List<SwiftMetaDataColumn> getRawColumns();

    abstract List<SwiftMetaDataColumn> getExtraColumns();

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

    @Override
    public LineParser getParser() {
        LineParser rawParser = new RawCSVParser(getRawColumns());
        List<SwiftMetaDataColumn> extra = getExtraColumns();
        LineAdapter adapter;
        if (extra.isEmpty()) {
            adapter = LineAdapter.DUMMY;
        } else {
            adapter = new AddColumnAdapter();
        }
        return new GeneralLineParser(getTableName(), appId, yearMonth, rawParser, adapter);
    }
}
