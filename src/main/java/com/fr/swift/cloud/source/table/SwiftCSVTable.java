package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.cloud.source.load.AddColumnAdapter;
import com.fr.swift.cloud.source.load.GeneralLineParser;
import com.fr.swift.cloud.source.load.LineAdapter;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.load.RawCSVParser;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class SwiftCSVTable implements CSVTable {

    private SwiftMetaData dbMetadata;
    private String appId;
    private String yearMonth;

    public SwiftCSVTable(SwiftMetaData dbMetadata, String appId, String yearMonth) {
        this.dbMetadata = dbMetadata;
        this.appId = appId;
        this.yearMonth = yearMonth;
    }

    @Override
    public LineParser getParser() {
        List<SwiftMetaDataColumn> extraFields = getExtraColumns();
        List<String> extraFieldNames = getExtraColumnNames();
        List<SwiftMetaDataColumn> selfBaseFields = new ArrayList<SwiftMetaDataColumn>();

        //package_info表自带appid和yearmonth，其他表需要拼接
        for (SwiftMetaDataColumn field : ((SwiftMetaDataBean) dbMetadata).getFields()) {
            if (extraFieldNames.contains(field.getName()) || field.getName().equals("yearMonth")) {
                continue;
            }
            if (!getTableName().equals("package_info")) {
                if (field.getName().equals("appId")) {
                    continue;
                }
            }
            selfBaseFields.add(field);
        }
        LineParser rawParser = new RawCSVParser(selfBaseFields);
        LineAdapter adapter;
        if (extraFields.isEmpty()) {
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

    @Override
    public String getTableName() {
        try {
            return dbMetadata.getTableName();
        } catch (SwiftMetaDataException ignore) {
            return null;
        }
    }

    @Override
    public SwiftMetaDataBean createBean(SwiftDatabase db) {
        return (SwiftMetaDataBean) dbMetadata;
    }

    public List<SwiftMetaDataColumn> getExtraColumns() {
        return Collections.EMPTY_LIST;
    }

    public List<String> getExtraColumnNames() {
        return Collections.EMPTY_LIST;
    }
}
