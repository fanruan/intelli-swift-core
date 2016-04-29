package com.fr.bi.conf.base.relation.path;

import com.fr.bi.conf.base.relation.BITableRelationTestTool;
import com.fr.bi.conf.base.relation.BITableTestTool;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/14.
 */
public class BIJuniorTableTest extends TestCase {
    private BIJuniorTablesManager juniorTablesManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        juniorTablesManager = new BIJuniorTablesManager();
    }

    public void testOneJunior() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            BITableContainer container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderOneJunior() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            BITableContainer container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void checkFourTable() {
        try {
            BITableContainer container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getD());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void checkFiveTable() {
        try {
            BITableContainer container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));

            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getD());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));
            container = juniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getE());
            assertTrue(container.contain(BITableTestTool.getB()));
            assertTrue(container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            assertTrue(container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testTwoJunior() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoJunior_____5() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoJunior_1() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoJunior__2() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoJunior___3() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoJunior____4() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * 测试倒V形状的结果
     */
    public void testVsharpJunior() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_1() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_2() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());

            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_3() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_4() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_5() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpJunior_6() {
        try {
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            juniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }
}