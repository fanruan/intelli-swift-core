package com.finebi.cube.gen.log;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.BINationTablesTest;

/**
 * Created by wuk on 16/6/6.
 */
public class LogManageTest extends BICubeTestBase {
    private BINationTablesTest biNationTablesTest;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        biNationTablesTest = new BINationTablesTest();
    }

    public void testLog() {
        biLogManager.logStart(-999);
        biNationTablesTest.testFieldPathIndex();
        biLogManager.logEnd(-999);
        try {
            assertTrue(biLogManager.createJSON(-999).getJSONArray("tables").length() == biNationTablesTest.getTablesAmount());
        } catch (Exception e) {
            assertFalse(true);
        }
    }
}
