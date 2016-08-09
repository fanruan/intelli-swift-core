package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.structure.column.date.BICubeDateColumn;
import com.finebi.cube.tools.BICubeResourceLocationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeColumnTest extends TestCase {

    public void testByteCubeColumnDetail() {

        BICubeByteColumn column = new BICubeByteColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class),
                BICubeResourceLocationTestTool.getBasic("testByteCubeColumnDetail"));
        column.addOriginalDataValue(2, Byte.valueOf("1"));
        assertEquals(Byte.valueOf("1"), column.getOriginalObjectValueByRow(2));
        assertEquals(Byte.valueOf("1").byteValue(), column.getOriginalValueByRow(2));
        try {
            column.addOriginalDataValue(-1, Byte.valueOf("1"));
        } catch (Exception e) {
            assertFalse(false);
            return;
        }
        assertFalse(true);
    }

    public void testByteCubeColumnGroup() {

        BICubeByteColumn column = new BICubeByteColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testByteCubeColumnGroup"));
        column.addGroupValue(2, Byte.valueOf("1"));
        assertEquals(Byte.valueOf("1").byteValue(), column.getGroupValue(2));
        assertEquals(Byte.valueOf("1"), column.getGroupObjectValue(2));
        try {
            column.addOriginalDataValue(-1, Byte.valueOf("1"));
        } catch (Exception e) {
            assertFalse(false);
            return;
        }
        assertFalse(true);
    }


    public void testIntegerCubeColumnDetail() {

        BICubeIntegerColumn column = new BICubeIntegerColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testIntegerCubeColumn"));
        column.addOriginalDataValue(1, 12);
        assertEquals(12, column.getOriginalValueByRow(1));
        assertEquals(Integer.valueOf(12), column.getOriginalObjectValueByRow(1));
    }

    public void testIntegerCubeColumnDetailNull() {

        BICubeIntegerColumn column = new BICubeIntegerColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testIntegerCubeColumn"));
        column.addOriginalDataValue(1, null);
        assertTrue(null == column.getOriginalObjectValueByRow(1));

    }

    public void testIntegerCubeColumnGroup() {

        BICubeIntegerColumn column = new BICubeIntegerColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testIntegerCubeColumn"));
        column.addGroupValue(1, 12);
        assertEquals(12, column.getGroupValue(1));
        assertEquals(Integer.valueOf(12), column.getGroupObjectValue(1));

    }

    public void testIntegerCubeColumnGroupNULL() {

        BICubeIntegerColumn column = new BICubeIntegerColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testIntegerCubeColumn"));
        column.addGroupValue(1, null);
        assertTrue(null == column.getGroupObjectValue(1));
    }

    public void testDoubleCubeColumnDetail() {

        BICubeDoubleColumn column = new BICubeDoubleColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDoubleCubeColumn"));
        column.addOriginalDataValue(1, Double.valueOf("12"));
        assertEquals(12, column.getOriginalValueByRow(1));
        assertEquals(Double.valueOf("12"), column.getOriginalObjectValueByRow(1));

    }

    public void testDoubleCubeColumnGroup() {

        BICubeDoubleColumn column = new BICubeDoubleColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDoubleCubeColumn"));
        column.addGroupValue(1, Double.valueOf("12"));
        assertEquals(12, column.getGroupValue(1));
        assertEquals(Double.valueOf("12"), column.getGroupObjectValue(1));
    }

    public void testDoubleCubeColumnGroupNULL() {

        BICubeDoubleColumn column = new BICubeDoubleColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDoubleCubeColumn"));
        column.addGroupValue(1, null);
        assertTrue(null == column.getGroupObjectValue(1));

    }

    public void testDoubleCubeColumnDetailNULL() {

        BICubeDoubleColumn column = new BICubeDoubleColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDoubleCubeColumn"));
        column.addOriginalDataValue(1, null);
        assertTrue(null == column.getOriginalObjectValueByRow(1));
    }

    public void testLongCubeColumnDetail() {

        BICubeLongColumn column = new BICubeLongColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testLongCubeColumn"));
        column.addOriginalDataValue(1, Long.valueOf("12"));
        assertEquals(12, column.getOriginalValueByRow(1));
        assertEquals(Long.valueOf("12"), column.getOriginalObjectValueByRow(1));
    }

    public void testLongCubeColumnDetailNULL() {

        BICubeLongColumn column = new BICubeLongColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testLongCubeColumn"));
        column.addOriginalDataValue(1, null);
        assertTrue(null == column.getOriginalObjectValueByRow(1));
    }

    public void testLongCubeColumnGroup() {

        BICubeLongColumn column = new BICubeLongColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testLongCubeColumn"));
        column.addGroupValue(1, Long.valueOf("12"));
        assertEquals(Long.valueOf("12"), column.getGroupObjectValue(1));
        assertEquals(12, column.getGroupValue(1));

    }

    public void testLongCubeColumnGroupNULL() {

        BICubeLongColumn column = new BICubeLongColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testLongCubeColumn"));
        column.addGroupValue(1, null);
        assertEquals(null, column.getGroupObjectValue(1));

    }

    /**
     * String
     */
    public void testStringCubeColumnDetail() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addOriginalDataValue(1, "abc");
        assertTrue("abc".equals(column.getOriginalValueByRow(1)));
        assertTrue("abc".equals(column.getOriginalObjectValueByRow(1)));
    }

    public void testStringCubeColumnDetailNull() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addOriginalDataValue(1, null);
        assertTrue(null == column.getOriginalValueByRow(1));
        assertTrue(null == column.getOriginalObjectValueByRow(1));
    }

    public void testStringCubeColumnDetailEmpty() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addOriginalDataValue(1, "");
        assertTrue("".equals(column.getOriginalValueByRow(1)));
        assertTrue("".equals(column.getOriginalObjectValueByRow(1)));
    }

    public void testStringCubeColumnGroup() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addGroupValue(1, "abc");
        assertTrue("abc".equals(column.getGroupValue(1)));
        assertTrue("abc".equals(column.getGroupObjectValue(1)));
    }

    public void testStringCubeColumnGroupNull() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addGroupValue(1, null);
        assertTrue(null == column.getGroupValue(1));
        assertTrue(null == column.getGroupObjectValue(1));
    }

    public void testStringCubeColumnGroupEmpty() {

        BICubeStringColumn column = new BICubeStringColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
        column.addGroupValue(1, "");
        assertTrue("".equals(column.getGroupValue(1)));
        assertTrue("".equals(column.getGroupObjectValue(1)));
    }

    public void testDateCubeColumnGroup() {

        BICubeDateColumn column = new BICubeDateColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDateCubeColumnDetail"));
        Long time = System.currentTimeMillis();
        column.addGroupValue(1, time);
        assertEquals(time.longValue(), column.getGroupValue(1));
        assertEquals(time, column.getGroupObjectValue(1));
    }

    public void testDateCubeColumnGroupEmptyNull() {

        BICubeDateColumn column = new BICubeDateColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testDateCubeColumnDetail"));
        column.addGroupValue(1, null);
        assertEquals(null, column.getGroupObjectValue(1));
    }

    public void testCubeColumnGroupSize() {

        BICubeLongColumn column = new BICubeLongColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), BICubeResourceLocationTestTool.getBasic("testLongCubeColumnGroup"));
        column.recordSizeOfGroup(10);
        assertEquals(10, column.sizeOfGroup());

    }


}
