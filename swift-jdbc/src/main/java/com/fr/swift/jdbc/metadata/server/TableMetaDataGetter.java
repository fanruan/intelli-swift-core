package com.fr.swift.jdbc.metadata.server;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.metadata.AbstractTableMetaDataGetter;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/9/4
 */
public class TableMetaDataGetter extends AbstractTableMetaDataGetter {
    private JdbcCaller caller;

    public TableMetaDataGetter(SwiftDatabase database, String tableName, JdbcCaller caller) {
        super(database, tableName);
        this.caller = caller;
    }

    @Override
    public SwiftMetaData get() {
        return caller.detectiveMetaData(database, tableName);
    }
}
