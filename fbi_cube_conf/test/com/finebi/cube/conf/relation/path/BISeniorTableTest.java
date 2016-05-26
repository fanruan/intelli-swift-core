package com.finebi.cube.conf.relation.path;

import com.finebi.cube.conf.relation.BITableRelationTestTool;
import com.finebi.cube.conf.relation.BITableTestTool;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/14.
 */
public class BISeniorTableTest extends TestCase {
    private BISeniorTablesManager seniorTablesManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        seniorTablesManager = new BISeniorTablesManager();
    }

    public void testOneSenior() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            BITableContainer container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderOneSenior() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            BITableContainer container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void checkFourTable() {
        try {
            BITableContainer container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(container.contain(BITableTestTool.getD()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            assertTrue(container.contain(BITableTestTool.getD()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            assertTrue(container.contain(BITableTestTool.getD()));
            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getD());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void checkFiveTable() {
        try {
            BITableContainer container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getA());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(container.contain(BITableTestTool.getE()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getB());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(container.contain(BITableTestTool.getE()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getC());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(container.contain(BITableTestTool.getD()));
            assertTrue(container.contain(BITableTestTool.getE()));

            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getD());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(container.contain(BITableTestTool.getE()));
            container = seniorTablesManager.getSpecificTableIndirectContainer(BITableTestTool.getE());
            assertTrue(!container.contain(BITableTestTool.getB()));
            assertTrue(!container.contain(BITableTestTool.getC()));
            assertTrue(!container.contain(BITableTestTool.getA()));
            assertTrue(!container.contain(BITableTestTool.getD()));
            assertTrue(!container.contain(BITableTestTool.getE()));

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testTwoSenior() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoSenior_____5() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoSenior_1() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoSenior__2() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoSenior___3() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderTwoSenior____4() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaAa());

            checkFourTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * 测试倒V形状的结果
     */
    public void testVsharpSenior() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_1() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_2() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());

            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_3() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_4() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_5() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());

            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDisorderVsharpSenior_6() {
        try {
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaDa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getEaAa());
            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getDaCa());

            seniorTablesManager.addBITableRelation(BITableRelationTestTool.getAaBa());

            checkFiveTable();

        } catch (Exception e) {
            assertTrue(false);
        }
    }
}