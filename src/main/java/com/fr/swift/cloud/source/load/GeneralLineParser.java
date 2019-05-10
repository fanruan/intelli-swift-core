package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.table.Execution;
import com.fr.swift.cloud.source.table.PackageInfo;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public class GeneralLineParser<T> implements LineParser<T> {

    private String tableName;
    private String appId;
    private String yearMonth;
    private LineParser<T> rawParser;
    private LineAdapter lineAdapter;

    public GeneralLineParser(String tableName, String appId, String yearMonth, LineParser rawParser, LineAdapter lineAdapter) {
        this.tableName = tableName;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.rawParser = rawParser;
        this.lineAdapter = lineAdapter;
    }

    @Override
    public Map<String, Object> parseToMap(T input) {
        Map<String, Object> raw = rawParser.parseToMap(input);
        if (raw == null) {
            return null;
        }
        raw = lineAdapter.adapt(raw);
        if (!tableName.equals(PackageInfo.tableName)) {
            raw.put(Execution.appId.getName(), appId);
        }
        raw.put(Execution.yearMonth.getName(), ParseUtils.convert(yearMonth, Execution.yearMonth));
        return raw;
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        columns.addAll(rawParser.getFields());
        columns.addAll(lineAdapter.getFields());
        if (!tableName.equals(PackageInfo.tableName)) {
            columns.add(Execution.appId);
        }
        columns.add(Execution.yearMonth);
        return columns;
    }
}
