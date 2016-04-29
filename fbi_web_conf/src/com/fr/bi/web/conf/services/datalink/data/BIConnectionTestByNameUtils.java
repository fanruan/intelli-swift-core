package com.fr.bi.web.conf.services.datalink.data;

import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;

/**
 * Created by Connery on 2014/11/24.
 */
public class BIConnectionTestByNameUtils extends BIConnectionTestUtils {

    @Override
    protected JDBCDatabaseConnection fetchConnection(String configData) throws Exception {
        DatasourceManagerProvider datasourceManager = DatasourceManager.getInstance();
        Iterator<String> nameIt = datasourceManager.getConnectionNameIterator();
        while (nameIt.hasNext()) {
            String n = nameIt.next();
            if (ComparatorUtils.equals(configData, n) && (datasourceManager.getConnection(n) instanceof JDBCDatabaseConnection)) {
                return (JDBCDatabaseConnection) datasourceManager.getConnection(n);
            }
        }
        return new JDBCDatabaseConnection();
    }
}