package com.fr.bi.stable.utils.code;

import junit.framework.TestCase;

/**
 * Created by wuk on 16/6/2.
 */
public class BILoggerTest extends TestCase{
    public void testAddLog() throws Exception {
        try {
            int a=1/0;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            assertTrue(BILogger.getLogger().getCubeLogInfo().getErrorMsg().length()>0);
        }
    }

    public void testGetLogInfo() throws Exception {

    }

}
