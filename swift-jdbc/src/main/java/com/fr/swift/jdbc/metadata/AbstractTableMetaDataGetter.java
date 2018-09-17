package com.fr.swift.jdbc.metadata;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.parser.SwiftMetaDataGetter;

/**
 * @author yee
 * @date 2018/9/4
 */
public abstract class AbstractTableMetaDataGetter implements SwiftMetaDataGetter {
    protected String tableName;
    protected SwiftDatabase database;

    public AbstractTableMetaDataGetter(SwiftDatabase database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }
}
