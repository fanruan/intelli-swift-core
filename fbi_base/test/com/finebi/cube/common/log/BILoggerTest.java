package com.finebi.cube.common.log;

import junit.framework.TestCase;

/**
 * This class created on 2016/10/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BILoggerTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/10/9
     */
    public void testBILogger() {
        try {
            BILogger logger_1 = BILoggerFactory.getLogger(BILogger.class);
            BILogger logger_2 = BILoggerFactory.getLogger();
            assertTrue(logger_1 == logger_2);

            BILogger logger_3 = BILoggerFactory.getLogger(BILoggerTest.class);
            BILogger logger_4 = BILoggerFactory.getLogger(BILoggerTest.class);
            assertTrue(logger_3 == logger_4);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
