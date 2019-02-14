package com.fr.swift.source.db;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.CoreField;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by pony on 2017/6/15.
 */
public class TableDBSource extends AbstractDBDataSource implements Serializable {
    private static final long serialVersionUID = -337260203343265208L;

    @CoreField
    protected String dbTableName;
    @CoreField
    protected String connection;

    public TableDBSource(String dbTableName, String connection) {
        this.dbTableName = dbTableName;
        this.connection = connection;
    }


    public TableDBSource(String dbTableName, String connection, Map<String, ColumnType> fieldColumnTypes) {
        super(fieldColumnTypes);
        this.dbTableName = dbTableName;
        this.connection = connection;
    }

    public String getConnectionName() {
        return connection;
    }

    public String getDBTableName() {
        return dbTableName;
    }

    @Override
    protected void initOuterMetaData() {
        ConnectionInfo connectionInfo = ConnectionManager.getInstance().getConnectionInfo(connection);
        outerMetaData = DBSourceUtils.getTableMetaData(connectionInfo.getFrConnection(), connectionInfo.getSchema(), dbTableName);
    }
}
