package com.fr.bi.web.services.util;

import com.fr.bi.conf.base.datasource.BIConnectOptimizationUtils4SQLSever;
import com.fr.data.impl.JDBCDatabaseConnection;
import junit.framework.TestCase;

/**
 * Created by Connery on 2014/11/17.
 */
public class BIConnectOptimizationUtils4SQLServerTest extends TestCase {
    public BIConnectPreconditon preconditon = new BIConnectPreconditon();

    /**
     * 测试
     */
    public void testOptimizeConnection() {
        BIConnectOptimizationUtils4SQLSever util = new BIConnectOptimizationUtils4SQLSever();
        JDBCDatabaseConnection connt = util.optimizeConnection(preconditon.getConnectionSQLServer_true());
        assertEquals(connt.getURL(), "jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;selectMethod=cursor");
        connt = util.optimizeConnection(preconditon.getConnectionSQLServer_null());
        assertSame(connt.getURL(), preconditon.getConnectionSQLServer_null().getURL());
        connt = util.optimizeConnection(preconditon.getConnectionSQLServer_zone());
        assertSame(connt.getURL(), preconditon.getConnectionSQLServer_zone().getURL());
        connt = util.optimizeConnection(preconditon.getConnectionSQLServer_URLWithoutSemicolon());
        assertEquals(connt.getURL(), "jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;selectMethod=cursor");
        connt = util.optimizeConnection(preconditon.getConnectionSQLServer_false());
        assertEquals(connt.getURL(), "jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;selectMethod=cursor");
        connt = util.optimizeConnection(preconditon.getConnectionSQLServer_WithSelectMethod());
        assertSame(connt.getURL(), preconditon.getConnectionSQLServer_WithSelectMethod().getURL());
    }

    /**
     * 复位
     */
    public void setUp() {
        preconditon.reset();
    }
}