package com.fr.swift.test.external;

import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;
import org.junit.rules.ExternalResource;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class ConnectionResource extends ExternalResource {

    @Override
    protected void before() {
        createConnection();
    }

    public static SwiftConnectionInfo createConnection() {
        //有这些表 BANK DEMO_CAPITAL_RETURN DEMO_CONTRACT DEMO_HR_SALESMAN DEMO_HR_USER DEMO_PRODUCT DEMO_REGION
        String path = ResourceUtils.getFileAbsolutePath("h2");
        Connection frConnection = new JDBCDatabaseConnection("org.h2.Driver", "jdbc:h2://" + path + "/test", "sa", "");
        SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
        ConnectionManager.getInstance().registerProvider(connectionName -> connectionInfo);
        return connectionInfo;
    }
}