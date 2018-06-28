package com.fr.swift.source.db;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.CoreField;

import java.io.Serializable;
import java.util.Map;

public class QueryDBSource extends AbstractDBDataSource implements Serializable {

    private static final long serialVersionUID = 4438891265082511219L;
    @CoreField
    protected String query;
    @CoreField
    protected String connection;

    public QueryDBSource(String query, String connection) {
        this.query = query;
        this.connection = connection;
    }

    public QueryDBSource(String query, String connection, Map<String, ColumnType> fieldColumnTypes) {
        super(fieldColumnTypes);
        this.query = query;
        this.connection = connection;
        this.fieldColumnTypes = fieldColumnTypes;
    }


    public String getQuery() {
        return query;
    }

    public String getConnectionName() {
        return connection;
    }

    @Override
    protected void initOuterMetaData() {
        ConnectionInfo connectionInfo = ConnectionManager.getInstance().getConnectionInfo(connection);
        outerMetaData = DBSourceUtils.getQueryMetaData(connectionInfo.getFrConnection(), query);
    }
}
