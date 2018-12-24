package com.fr.swift.source.resultset;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseStreamResultSet<Paths> implements SwiftStreamResultSet {
    protected Paths paths;
    protected LineParser parser;
    protected SwiftMetaData metaData;
    private static final Pattern PATTERN = Pattern.compile("(http|https)+://");

    public BaseStreamResultSet(Paths paths, LineParser parser) {
        this.paths = paths;
        this.parser = parser;
    }

    public BaseStreamResultSet(LineParser parser) {
        this.parser = parser;
    }

    public BaseStreamResultSet(SwiftMetaData metaData, LineParser parser) {
        this.parser = parser;
        this.metaData = metaData;
    }

    public BaseStreamResultSet(SwiftMetaData metaData, Paths paths, LineParser parser) {
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
        metaData = new SwiftMetaDataBean(tableName, database, null, tableName, null, columns);
        SwiftContext.get().getBean(SwiftMetaDataService.class).saveOrUpdate(metaData);
    }
}
