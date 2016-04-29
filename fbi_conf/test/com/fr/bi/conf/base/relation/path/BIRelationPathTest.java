package com.fr.bi.conf.base.relation.path;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.relation.BITableRelationAnalysisService;
import com.fr.bi.conf.base.relation.BITableRelationTestTool;
import com.fr.bi.conf.base.relation.BITableTestTool;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.Set;

/**
 * Created by Connery on 2016/2/23.
 */
public class BIRelationPathTest extends TestCase {
    private BITablePathAnalysersService pathAnalysersService;
    private BITableRelationAnalysisService biTableRelationAnalyser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        biTableRelationAnalyser = BIFactoryHelper.getObject(BITableRelationAnalysisService.class);
        pathAnalysersService = new BITablePathAnalysersManager(biTableRelationAnalyser);
    }

    private void addRelation(BITableRelation relation) throws BIRelationDuplicateException {
        pathAnalysersService.registerBITableRelation(relation);
        biTableRelationAnalyser.addRelation(relation);
    }

    public void testGetTwoAvailablePath() {
        try {
            addRelation(BITableRelationTestTool.getCaBa());
            addRelation(BITableRelationTestTool.getAaBa());
            addRelation(BITableRelationTestTool.getDaAa());
            addRelation(BITableRelationTestTool.getDaCa());
            Set<BITableRelationPath> relationSet = pathAnalysersService.analysisAllPath(new BITablePair(
                    BITableTestTool.getD()
                    , BITableTestTool.getB())

            );
            assertEquals(relationSet.size(), 2);
            BITableRelationPath D_A_B = new BITableRelationPath();
            D_A_B.addRelationAtHead(BITableRelationTestTool.getAaBa());
            D_A_B.addRelationAtHead(BITableRelationTestTool.getDaAa());
            assertTrue(relationSet.contains(D_A_B));

            BITableRelationPath D_C_B = new BITableRelationPath();
            D_C_B.addRelationAtHead(BITableRelationTestTool.getCaBa());
            D_C_B.addRelationAtHead(BITableRelationTestTool.getDaCa());
            assertTrue(relationSet.contains(D_C_B));

        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
            assertTrue(false);
        }
    }

    public void testGetOneTableInTwoPath() {
        try {
            addRelation(BITableRelationTestTool.getAaBa());
            addRelation(BITableRelationTestTool.getBcCc());
            Set<BITableRelationPath> relationSet = pathAnalysersService.analysisAllPath(new BITablePair(
                    BITableTestTool.getB()
                    , BITableTestTool.getB())
            );
            assertEquals(relationSet.size(), 0);
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
            assertTrue(false);
        }
    }

    public void testGetOneTableInTwoPathIncludingCircle() {
        try {
            addRelation(BITableRelationTestTool.getAaBa());
            addRelation(BITableRelationTestTool.getBcCc());
            addRelation(BITableRelationTestTool.getCaAa());
            Set<BITableRelationPath> relationSet = pathAnalysersService.analysisAllPath(new BITablePair(
                    BITableTestTool.getB()
                    , BITableTestTool.getB())

            );
            assertEquals(relationSet.size(), 1);
            BITableRelationPath B_C_A_B = new BITableRelationPath();
            B_C_A_B.addRelationAtHead(BITableRelationTestTool.getAaBa());
            B_C_A_B.addRelationAtHead(BITableRelationTestTool.getCaAa());
            B_C_A_B.addRelationAtHead(BITableRelationTestTool.getBcCc());
            assertTrue(relationSet.contains(B_C_A_B));
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
            assertTrue(false);
        }
    }

    public void testGetTwoPartCoverAvailablePath() {
        try {
            addRelation(BITableRelationTestTool.getDaCa());
            addRelation(BITableRelationTestTool.getAaEa());
            addRelation(BITableRelationTestTool.getCaEa());
            addRelation(BITableRelationTestTool.getDaAa());
            addRelation(BITableRelationTestTool.getEaBa());


            Set<BITableRelationPath> relationSet = pathAnalysersService.analysisAllPath(new BITablePair(
                    BITableTestTool.getD()
                    , BITableTestTool.getB())

            );
            assertEquals(relationSet.size(), 2);
            BITableRelationPath D_A_E_B = new BITableRelationPath();
            D_A_E_B.addRelationAtHead(BITableRelationTestTool.getEaBa());
            D_A_E_B.addRelationAtHead(BITableRelationTestTool.getAaEa());
            D_A_E_B.addRelationAtHead(BITableRelationTestTool.getDaAa());
            assertTrue(relationSet.contains(D_A_E_B));

            BITableRelationPath D_C_E_B = new BITableRelationPath();
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getEaBa());
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getCaEa());
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getDaCa());
            assertTrue(relationSet.contains(D_C_E_B));

        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
            assertTrue(false);
        }
    }

    public void testGetThreeAvailablePath() {
        try {
            addRelation(BITableRelationTestTool.getCaEa());
            addRelation(BITableRelationTestTool.getEaAa());
            addRelation(BITableRelationTestTool.getAaBa());
            addRelation(BITableRelationTestTool.getEaBa());
            addRelation(BITableRelationTestTool.getDaAa());
            addRelation(BITableRelationTestTool.getDaCa());
            Set<BITableRelationPath> relationSet = pathAnalysersService.analysisAllPath(new BITablePair(
                    BITableTestTool.getD()
                    , BITableTestTool.getB())

            );
            assertEquals(relationSet.size(), 3);
            BITableRelationPath D_A_B = new BITableRelationPath();
            D_A_B.addRelationAtHead(BITableRelationTestTool.getAaBa());
            D_A_B.addRelationAtHead(BITableRelationTestTool.getDaAa());
            assertTrue(relationSet.contains(D_A_B));

            BITableRelationPath D_C_E_B = new BITableRelationPath();
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getEaBa());
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getCaEa());
            D_C_E_B.addRelationAtHead(BITableRelationTestTool.getDaCa());
            assertTrue(relationSet.contains(D_C_E_B));

            BITableRelationPath D_C_E_A_B = new BITableRelationPath();
            D_C_E_A_B.addRelationAtHead(BITableRelationTestTool.getAaBa());
            D_C_E_A_B.addRelationAtHead(BITableRelationTestTool.getEaAa());
            D_C_E_A_B.addRelationAtHead(BITableRelationTestTool.getCaEa());
            D_C_E_A_B.addRelationAtHead(BITableRelationTestTool.getDaCa());
            assertTrue(relationSet.contains(D_C_E_B));
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
            assertTrue(false);
        }
    }
}