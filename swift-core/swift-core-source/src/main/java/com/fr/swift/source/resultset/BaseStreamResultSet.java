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
    public static final String NUMBER_REG = "^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$";
    protected Paths paths;
    protected LineParser parser;
    protected SwiftMetaData metaData;
    private Pattern pattern = Pattern.compile("(http|https)+://");

    public BaseStreamResultSet(SwiftDatabase database, String tableName, Paths paths, LineParser parser) {
        this.paths = paths;
        this.parser = parser;
    }

    public BaseStreamResultSet(SwiftMetaData metaData, Paths paths, LineParser parser) {
        this.paths = paths;
        this.parser = parser;
        this.metaData = metaData;
    }

    protected InputStream getInputStream(String path) throws Exception {
        if (pattern.matcher(path.toLowerCase()).matches()) {
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
