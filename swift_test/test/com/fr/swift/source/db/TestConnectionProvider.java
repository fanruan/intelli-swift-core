package com.fr.swift.source.db;

import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.swift.resource.ResourceUtils;

/**
 * Created by pony on 2017/12/29.
 *
 */
public class TestConnectionProvider {
    public static SwiftConnectionInfo createConnection() {
        //有这些表 BANK DEMO_CAPITAL_RETURN DEMO_CONTRACT DEMO_HR_SALESMAN DEMO_HR_USER DEMO_PRODUCT DEMO_REGION
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
        Connection frConnection = new JDBCDatabaseConnection("org.h2.Driver", "jdbc:h2://" + path + "/test", "sa", "");
        SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
        ConnectionManager.getInstance().registerProvider(new IConnectionProvider() {
            @Override
            public ConnectionInfo getConnection(String connectionName) {
                return connectionInfo;
            }

            @Override
            public void register(String connectionName, ConnectionInfo connectionInfo) {

            }
        });
        return connectionInfo;
    }
}
