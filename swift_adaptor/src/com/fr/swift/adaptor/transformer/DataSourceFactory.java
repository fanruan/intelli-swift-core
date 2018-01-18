package com.fr.swift.adaptor.transformer;

import com.fr.data.impl.Connection;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.source.db.*;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.List;

/**
 * This class created on 2017-12-19 09:12:11
 *
 * @author Lucifer
 * @description FineCubeTable-->DataSource
 * @since Advanced FineBI Analysis 1.0
 */
public class DataSourceFactory {


    public static TableDBSource transformTableDBSource(String connectionName, String dbTableName, String schema, Connection connection) {
        IConnectionProvider connectionProvider = new ConnectionProvider();
        //调用swift可以在此调试
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, new SwiftConnectionInfo(schema, connection));
        TableDBSource tableDBSource = new TableDBSource(dbTableName, connectionName);
        return tableDBSource;
    }

    public static QueryDBSource transformQueryDBSource(String connectionName, String sql, String schema, Connection connection) {
        IConnectionProvider connectionProvider = new ConnectionProvider();
        //调用swift可以在此调试
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, new SwiftConnectionInfo(schema, connection));
        QueryDBSource queryDBSource = new QueryDBSource(sql, connectionName);
        return queryDBSource;
    }

    public static ExcelDataSource transformExcelDataSource(String path, String[] names, int[] types, List<String> appendedFileNames) {
        ExcelDataSource excelDataSource = new ExcelDataSource(path, names, types, appendedFileNames);
        return excelDataSource;
    }
}

