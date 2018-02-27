package com.fr.swift.generate;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

public abstract class BaseTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        new LocalSwiftServerService().start();
        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + "resources" + "\\" + System.currentTimeMillis()));
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        ConnectionManager.getInstance().registerConnectionInfo("allTest", TestConnectionProvider.createConnection());
    }
}
