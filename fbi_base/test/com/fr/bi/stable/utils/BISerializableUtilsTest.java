package com.fr.bi.stable.utils;

import com.fr.bi.stable.utils.serializable.*;
import junit.framework.TestCase;

/**
 * This class created on 2016/11/23.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BISerializableUtilsTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testBasicClassSerial() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerial.class).size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testBasicClassUnSerial() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassUnSerial.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testBasicInterUnSerial() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(InterfaceUnSerial.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testBasicInterSerial() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(InterfaceSerial.class).size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassAttUn() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerialAttUn.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassRecursive() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassRecursiveAttUnOne.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassSerialInterUn() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerialInterUn.class).size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassSerialSuperUn() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerialSuperUn.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassSerialAttInterUn() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerialAttInterUn.class).size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassSerialAttInterSerial() {
        try {
            assertEquals(BISerializableUtils.findUnsupportedSerializable(ClassSerialAttInterSerial.class).size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testInterHasSuper() {
        try {
            assertEquals(BISerializableUtils.hasSuper(InterfaceSerial.class), false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassHasSuper() {
        try {
            assertEquals(BISerializableUtils.hasSuper(ClassSerialSuperUn.class), true);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/11/23
     */
    public void testClassNoSuper() {
        try {
            assertEquals(BISerializableUtils.hasSuper(ClassSerial.class), false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
