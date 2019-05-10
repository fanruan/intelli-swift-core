package com.fr.swift.cloud.source.table;

import com.fr.swift.cloud.source.BaseTable;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.cloud.source.load.AddColumnAdapter;
import com.fr.swift.cloud.source.load.GeneralLineParser;
import com.fr.swift.cloud.source.load.LineAdapter;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.load.RawCSVParser;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
@Deprecated
abstract class CSVBaseTable extends BaseTable implements CSVTable {

    private String appId;
    private String yearMonth;

    public CSVBaseTable(String appId, String yearMonth) {
        this.appId = appId;
        this.yearMonth = yearMonth;
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

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.CSV;
    }
}
