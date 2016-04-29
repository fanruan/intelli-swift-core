package com.fr.bi.conf.base.relation.path;

import com.fr.bi.conf.base.relation.BITableRelationTestTool;
import com.fr.bi.conf.base.relation.BITableTestTool;
import com.fr.bi.conf.base.relation.BIUserTableRelationManager;
import com.fr.bi.conf.base.relation.BIUserTableRelationManagerTestTool;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/14.
 */
public class BICommonSeniorTableTest extends TestCase {
    private BIUserTableRelationManager tableRelationManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableRelationManager = BIUserTableRelationManagerTestTool.generateUserTableRelationManager();
    }

    public void testOneCommonSenior() {
        try {
            tableRelationManager.registerTableRelation(BITableRelationTestTool.getAaBa());
            tableRelationManager.registerTableRelation(BITableRelationTestTool.getAaCa());
            BITableContainer tableContainer = tableRelationManager.getCommonSeniorTables(new BITablePair(BITableTestTool.getB(), BITableTestTool.getC()));
            assertTrue(tableContainer.contain(BITableTestTool.getA()));
            assertFalse(tableContainer.contain(BITableTestTool.getB()));
            assertFalse(tableContainer.contain(BITableTestTool.getC()));

        } catch (Exception e) {
            BILogger.getLogger().error("", e);
            assertTrue(false);
        }
    }

    public void testTwoCommonSenior() {
        try {
            tableRelationManager.registerTableRelation(BITableRelationTestTool.getDaAa());
            tableRelationManager.registerTableRelation(BITableRelationTestTool.getAaBa());
            tableRelationManager.registerTableRelation(BITableRelationTestTool.getAaCa());
            BITableContainer tableContainer = tableRelationManager.getCommonSeniorTables(new BITablePair(BITableTestTool.getB(), BITableTestTool.getC()));
            assertTrue(tableContainer.contain(BITableTestTool.getA()));
            assertFalse(tableContainer.contain(BITableTestTool.getB()));
            assertFalse(tableContainer.contain(BITableTestTool.getC()));
            assertTrue(tableContainer.contain(BITableTestTool.getD()));


        } catch (Exception e) {
            BILogger.getLogger().error("", e);
            assertTrue(false);
        }
    }
}