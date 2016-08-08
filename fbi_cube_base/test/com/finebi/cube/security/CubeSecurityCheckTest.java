package com.finebi.cube.security;

import junit.framework.TestCase;

import java.io.File;

/**
 * TODO 有环境依赖
 * This class created on 2016/7/31.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeSecurityCheckTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/31
     */
    public void testComputeSum() {
        try {
            String path = this.getClass().getResource("/").getPath();
            CubeSecurityCheck securityCheck = new CubeSecurityCheck(path + "crc_sum");
            Boolean check = securityCheck.check("D:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes\\D");
            assertTrue(check);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/31
     */
    public void testSaveSum() {
        try {
            String path = this.getClass().getResource("/").getPath();
            CubeSecurityCheck securityCheck = new CubeSecurityCheck(path + "crc_sum");
            securityCheck.removeResult();
            assertFalse(new File(path + "crc_sum").exists());
            securityCheck.check("C:\\Users\\wuk\\Documents\\fineBI\\env\\WebReport\\WEB-INF\\resources\\cubes\\-999\\Advanced");
            securityCheck.useLeastSum();
            securityCheck.saveResult();
            assertTrue(new File(path + "crc_sum").exists());
            assertTrue(securityCheck.check("C:\\Users\\wuk\\Documents\\fineBI\\env\\WebReport\\WEB-INF\\resources\\cubes\\-999\\Advanced"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/31
     */
    public void testCheckSum() {
        try {
            String path = this.getClass().getResource("/").getPath();
            CubeSecurityCheck securityCheck = new CubeSecurityCheck(path + "crc_sum");
            assertTrue(securityCheck.check("D:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes\\D"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/31
     */
    public void testCheckWrongSum() {
        try {
            String path = "F:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes\\D";
            CubeSecurityCheck securityCheck = new CubeSecurityCheck(path + "crc_sum");
            assertTrue(securityCheck.check("D:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes\\D"));
         
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/31
     */
    public void testCheckSum_Not() {
        try {
            String path = this.getClass().getResource("/").getPath();
            CubeSecurityCheck securityCheck = new CubeSecurityCheck(path + "crc_sum");
            assertFalse(securityCheck.check("D:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes\\D"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
