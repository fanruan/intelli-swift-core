package com.fr.swift.cloud.source.table;

import com.fr.swift.cloud.source.BaseTable;
import com.fr.swift.cloud.source.load.AddColumnAdapter;
import com.fr.swift.cloud.source.load.GeneralLineParser;
import com.fr.swift.cloud.source.load.LineAdapter;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.load.RawGCParser;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.io.File;
import java.util.List;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public abstract class GCBaseTable extends BaseTable implements CloudTable {
    private String appId;
    private String yearMonth;
    private File[] files;

    public GCBaseTable(String appId, String yearMonth, File[] files) {
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.files = files;
    }

    public File[] getFiles() {
        return files;
    }


    @Override
    public LineParser getParser() {
        LineParser rawParser = new RawGCParser(getRawColumns());
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