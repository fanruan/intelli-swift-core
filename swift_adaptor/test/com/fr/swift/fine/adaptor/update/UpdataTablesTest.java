package com.fr.swift.fine.adaptor.update;

import com.finebi.conf.internalimp.connection.FineConnectionImp;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import junit.framework.TestCase;

/**
 * This class created on 2018-1-12 16:25:06
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class UpdataTablesTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
        FineConnection connection = new FineConnectionImp("jdbc:h2://" + path + "/test", "sa", "", "org.h2.Driver", "local", null, null, null);
        FineConnectionUtils.removeAllConnections();
        FineConnectionUtils.addNewConnection(connection);
    }
}
