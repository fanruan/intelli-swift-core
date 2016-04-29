package com.fr.bi.web.services.util;

import junit.framework.TestCase;

/**
 * Created by Connery on 2014/11/17.
 */
public class BiConnectionOptimizeUtilsFactoryTest extends TestCase {

    public BIConnectPreconditon preconditon = new BIConnectPreconditon();

    /**
     * 测试
     */
    public void testGetOptimizationUtils() {
        BIConnectOptimizationUtils utils_1 = BIConnectOptimizationUtilsFactory.getOptimizationUtils(preconditon.getConnectionSQLServer_true());
        assertTrue(utils_1 instanceof BIConnectOptimizationUtils4SQLSever);
        utils_1 = BIConnectOptimizationUtilsFactory.getOptimizationUtils(preconditon.getConnectionSQLServer_null());
        assertTrue(utils_1 instanceof BIConnectOptimizationUtils);
        utils_1 = BIConnectOptimizationUtilsFactory.getOptimizationUtils(preconditon.getConnectionSQLServer_zone());
        assertTrue(utils_1 instanceof BIConnectOptimizationUtils);
        utils_1 = BIConnectOptimizationUtilsFactory.getOptimizationUtils(preconditon.getConnectionSQLServer_false());
        assertTrue(utils_1 instanceof BIConnectOptimizationUtils);
    }

    /**
     * 测试
     */
    public void setUp() {
        preconditon.reset();
    }
}