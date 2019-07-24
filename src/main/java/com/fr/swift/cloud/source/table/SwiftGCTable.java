package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.cloud.source.load.GeneralLineParser;
import com.fr.swift.cloud.source.load.LineAdapter;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.cloud.source.load.RawGCParser;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/5/17
 *
 * @author Lucifer
 * @description
 */
public class SwiftGCTable implements CloudTable {

    private SwiftMetaData dbMetadata;
    private SwiftMetaData versionMetadata;
    private String appId;
    private String yearMonth;

    public SwiftGCTable(SwiftMetaData dbMetadata, SwiftMetaData versionMetadata, String appId, String yearMonth) {
        this.dbMetadata = dbMetadata;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.versionMetadata = versionMetadata;
    }

    @Override
    public LineParser getParser() {
        List<SwiftMetaDataColumn> selfBaseFields = new ArrayList<SwiftMetaDataColumn>();
        for (SwiftMetaDataColumn field : ((SwiftMetaDataBean) versionMetadata).getFields()) {
            selfBaseFields.add(field);
        }
        LineParser rawParser = new RawGCParser(selfBaseFields);
        LineAdapter adapter = LineAdapter.DUMMY;
        return new GeneralLineParser(getTableName(), appId, yearMonth, rawParser, adapter);
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.CSV;
    }

    @Override
    public String getTableName() {
        try {
            return versionMetadata.getTableName();
        } catch (SwiftMetaDataException ignore) {
            return null;
        }
    }

    @Override
    public SwiftMetaDataBean getDBMetadata() {
        return (SwiftMetaDataBean) dbMetadata;
    }

    @Override
    public SwiftMetaDataBean getVersionMetadata() {
        return (SwiftMetaDataBean) versionMetadata;
    }

    public List<SwiftMetaDataColumn> getExtraColumns() {
        return Collections.EMPTY_LIST;
    }

    public List<String> getExtraColumnNames() {
        return Collections.EMPTY_LIST;
    }

}