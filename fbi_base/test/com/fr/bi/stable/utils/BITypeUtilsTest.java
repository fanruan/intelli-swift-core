package com.fr.bi.stable.utils;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.bi.stable.utils.program.BITypeUtils;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/4.
 */
public class BITypeUtilsTest extends TestCase {
    public void testLong2Int() {
        long[] longs = new long[1];
        longs[0] = BIRandomUitils.getRandomLong();
        boolean flag = true;
        try {
            BITypeUtils.turnToIntArrayWithCheck(longs);
        } catch (Exception ex) {
            flag = false;
        }
        assertFalse(flag);
        try {
            flag = true;
            longs[0] = 123;
            BITypeUtils.turnToIntArrayWithCheck(longs);
        } catch (Exception ex) {

        }
        assertTrue(flag);
    }

    public void testPrimitive() {

        assertTrue(BITypeUtils.isPrimitiveType(int.class));
        assertTrue(BITypeUtils.isPrimitiveType(boolean.class));
        assertTrue(BITypeUtils.isPrimitiveType(char.class));
        assertTrue(BITypeUtils.isPrimitiveType(long.class));
        assertTrue(BITypeUtils.isPrimitiveType(float.class));
        assertTrue(BITypeUtils.isPrimitiveType(double.class));
        assertTrue(BITypeUtils.isPrimitiveType(byte.class));
        assertTrue(BITypeUtils.isPrimitiveType(short.class));
        assertFalse(BITypeUtils.isPrimitiveType(BITypeUtilsTest.class));
        assertTrue(BITypeUtils.isAutoBoxType(Integer.class));
        assertTrue(BITypeUtils.isAutoBoxType(Boolean.class));
        assertTrue(BITypeUtils.isAutoBoxType(Character.class));
        assertTrue(BITypeUtils.isAutoBoxType(Long.class));
        assertTrue(BITypeUtils.isAutoBoxType(Float.class));
        assertTrue(BITypeUtils.isAutoBoxType(Double.class));
        assertTrue(BITypeUtils.isAutoBoxType(Byte.class));
        assertTrue(BITypeUtils.isAutoBoxType(Short.class));
        assertFalse(BITypeUtils.isAutoBoxType(BITypeUtilsTest.class));
    }

    public void testStringConvert() {
//        int value =
        assertEquals((int) BITypeUtils.stringConvert2BasicType(int.class, "12"), Integer.valueOf(12).intValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Integer.class, "12"), Integer.valueOf(12));
        assertEquals(BITypeUtils.stringConvert2BasicType(double.class, "12"), Double.valueOf(12).doubleValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Double.class, "12"), Double.valueOf(12));
        assertEquals(BITypeUtils.stringConvert2BasicType(float.class, "12"), Float.valueOf(12).floatValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Float.class, "12"), Float.valueOf(12));
        assertEquals((long) BITypeUtils.stringConvert2BasicType(long.class, "12"), Long.valueOf(12).longValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Long.class, "12"), Long.valueOf(12));
        assertEquals((boolean) BITypeUtils.stringConvert2BasicType(boolean.class, "true"), Boolean.valueOf(true).booleanValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Boolean.class, "false"), Boolean.valueOf(false));
        assertEquals((byte) BITypeUtils.stringConvert2BasicType(byte.class, "123"), Byte.valueOf("123").byteValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Byte.class, "123"), Byte.valueOf("123"));
        assertEquals((char) BITypeUtils.stringConvert2BasicType(char.class, "cfffff"), Character.valueOf('c').charValue());
        assertEquals(BITypeUtils.stringConvert2BasicType(Character.class, "c"), Character.valueOf('c'));
        assertEquals(BITypeUtils.stringConvert2BasicType(Character.class, "null"), null);

    }

    public void testBasicTypeCovert2String() {
//        assertEquals(BITypeUtils.basicValueConvert2String(1), "1");
    }
}