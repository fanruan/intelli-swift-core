package com.fr.swift.adaptor.transformer;

import com.fr.data.impl.Connection;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;

/**
 * This class created on 2017-12-19 09:12:11
 *
 * @author Lucifer
 * @description FineCubeTable-->DataSource
 * @since Advanced FineBI Analysis 1.0
 */
public class DataSourceFactory {

    
    public static TableDBSource transformTableDBSource(String connectionName, String dbTableName, String schema, Connection connection) {

        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
                new SwiftConnectionInfo(schema, connection));

        TableDBSource tableDBSource = new TableDBSource(dbTableName, connectionName);
        return tableDBSource;
    }

//    private static QueryDBSource transformQueryDBSource(FineSQLCubeTable cubeTable) {
//        String connectionName = cubeTable.getConnectionName();
//        String query = cubeTable.getQuery();
//
//        FineConnectionInfo fineConnectionInfo = cubeTable.getUsedConnectionInfo();
//        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
//                new SwiftConnectionInfo(fineConnectionInfo.getSchema(), fineConnectionInfo.getFRConnection()));
//
//        QueryDBSource queryDBSource = new QueryDBSource(query, connectionName);
//        return queryDBSource;
//    }

}

