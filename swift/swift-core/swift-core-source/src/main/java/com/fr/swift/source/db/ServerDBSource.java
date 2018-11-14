package com.fr.swift.source.db;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.CoreField;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Roy on 2017/6/16.
 */
public class ServerDBSource extends AbstractDBDataSource implements Serializable {


    private static final long serialVersionUID = -2942727704344267855L;

    @CoreField
    protected String serverTableName;

    public ServerDBSource(String serverTableName) {
        this.serverTableName = serverTableName;
    }

    public ServerDBSource(String serverTableName, Map<String, ColumnType> fieldColumnTypes) {
        super(fieldColumnTypes);
        this.serverTableName = serverTableName;
    }

    public String getServerTableName() {
        return serverTableName;
    }

    @Override
    protected void initOuterMetaData() {
        outerMetaData =  DBSourceUtils.getServerMetaData(serverTableName);
    }
}
