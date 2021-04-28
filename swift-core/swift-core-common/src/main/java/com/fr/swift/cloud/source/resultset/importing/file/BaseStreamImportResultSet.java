package com.fr.swift.cloud.source.resultset.importing.file;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.source.resultset.importing.BaseImportResultSet;
import com.fr.swift.cloud.source.resultset.importing.ImportType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yee
 * @date 2018-12-10
 */
public abstract class BaseStreamImportResultSet<Paths> extends BaseImportResultSet {
    protected Paths paths;
    protected FileLineParser parser;
    private static final Pattern PATTERN = Pattern.compile("(http|https)+://");
    protected SwiftMetaData metaData;

    public BaseStreamImportResultSet(Paths paths, FileLineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.paths = paths;
        this.parser = parser;
    }

    public BaseStreamImportResultSet(FileLineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.parser = parser;
    }

    public BaseStreamImportResultSet(SwiftMetaData metaData, FileLineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.parser = parser;
        this.metaData = metaData;
    }

    public BaseStreamImportResultSet(SwiftMetaData metaData, Paths paths, FileLineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.paths = paths;
        this.parser = parser;
        this.metaData = metaData;
    }

    protected InputStream getInputStream(String path) throws Exception {
        if (PATTERN.matcher(path.toLowerCase()).matches()) {
            return new URL(path).openStream();
        }
        return new FileInputStream(path);
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    protected void initMetaData(SwiftDatabase database, String tableName, List<SwiftMetaDataColumn> columns) {
        metaData = new SwiftMetaDataEntity(tableName, database, null, tableName, null, columns);
        SwiftContext.get().getBean(SwiftMetaDataService.class).saveMeta(metaData);
    }
}
