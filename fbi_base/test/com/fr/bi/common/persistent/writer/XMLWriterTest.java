package com.fr.bi.common.persistent.writer;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLNormalValueReader;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLNormalValueWriter;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.bi.common.world.BookRack;
import com.fr.bi.common.world.people.Student;
import com.fr.bi.stable.constant.BIXMLConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.xml.XMLTools;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLWriterTest extends TestCase {

    public void testArrayNull() {
        try {
            ArrayPart partNull = ArrayPart.generateNull();
            checkEquals(partNull, "arrayNull");
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);

        }
    }

    public void testArrayOnePerson() {

        ArrayPart part = ArrayPart.generatePersonOne();
        checkEquals(part, "arrayPersonOne");
    }

    public void testArrayTwoPerson() {

        ArrayPart part = ArrayPart.generatePersonTwo();
        checkEquals(part, "arrayPersonTwo");
    }

    public void testArrayTwo() {

        ArrayPart part = ArrayPart.generateTwo();
        checkEquals(part, "arrayTwo");
    }

    public void testArrayBasic() {

        try {
            ArrayPart part = ArrayPart.generateBasicOne();
            checkEquals(part, "arrayBasic");
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    public void testArrayBasicTwo() {
        ArrayPart part = ArrayPart.generateBasicTwo();
        checkEquals(part, "ArrayBasicTwo");
    }

    private void generate(Object target, String name) {
        try {
            BIBeanXMLWriterWrapper wrapper = new BIBeanXMLWriterWrapper(target);
            wrapper.setTag(name);
            wrapper.setTagAvailable(true);
            wrapper.setProperty(false);
            XMLPersistentWriter object = new XMLPersistentWriter(wrapper);
            File var3 = new File("E:\\temp\\" + name + ".xml");
            StableUtils.makesureFileExist(var3);
            XMLTools.writeOutputStreamXML(object, new FileOutputStream(var3));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    public void testMapBasicTwo() {
        MapPart part = MapPart.generateBasicTwo();
        generate(part, "MapBasicTwo");
    }

    public void testMapPersonTwo() {
        MapPart part = MapPart.generatePersonTwo();
        checkEquals(part, "MapPersonTwo");
    }

    public void testMapTwo() {
        MapPart part = MapPart.generateTwo();
        checkEquals(part, "MapTwo");

    }

    public void testIteratorPersonTwo() {
        IterablePart part = IterablePart.generateTwoPersonIter();
        generate(part, "IteratorPersonTwo");
    }

    public void testIteratorPersonTwoConnect() {
        IterablePart part = IterablePart.generateTwoPersonConnect();
        generate(part, "IteratorPersonTwoConnect");
    }

    public void testNormalOne() {
        NormalPart part = NormalPart.generateOneContent();
        generate(part, "NormalOne");
    }

    public void testNormalOnePerson() {
        NormalPart part = NormalPart.generateOnePerson();
        checkEquals(part, "NormalOnePerson");
    }

    /**
     * 将目标对象生成xml，再依据xml构造一个新的对象，
     * 两个对象进行严格的对象判断。
     *
     * @param obj  目标对象
     * @param name xml保存的名字
     */
    private void checkEquals(Object obj, String name) {
        try {
            generate(obj, name);
            Class objClass = Class.forName(obj.getClass().getName());
            Object o;
            o = BIConstructorUtils.forceConstructObject(objClass);
            o = get(o, name);
            assertTrue(BIComparatorUtils.isExactlyEquals(o, obj));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);

        }
    }

    /**
     * 将目标对象生成xml，再依据xml构造一个新的对象，
     * 两个对象进行严格的对象判断。
     *
     * @param obj  目标对象
     * @param name xml保存的名字
     */
    private void checkArrayEquals(Object obj, String name, int size) {
        try {
            generate(obj, name);
            Class objClass = Class.forName(obj.getClass().getName());
            Object o;
            o = BIConstructorUtils.constructArrayObject(objClass.getComponentType(), size);
            o = get(o, name);
            assertTrue(BIComparatorUtils.isExactlyEquals(o, obj));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);

        }
    }

    private Object get(Object object, String name) {
        try {
            File var3 = new File("E:\\temp\\" + name + ".xml");
            StableUtils.makesureFileExist(var3);
            XMLPersistentReader reader = new XMLPersistentReader(new HashMap<String, BIBeanXMLReaderWrapper>(), new BIBeanXMLReaderWrapper(object));
            XMLTools.readInputStreamXML(reader, new FileInputStream(var3));
            return object;
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return object;
    }

    public void testReadTwoMapBasic() {
        MapPart part = new MapPart();
        Object o = get(part, "MapBasicTwo");
        BILoggerFactory.getLogger().info(o.toString());
    }

    public void testIterationParts() {
        IterablePart part = IterablePart.generateParts();
        checkEquals(part, "IterationParts");

    }

    public void testListList() {
        IterablePart part = IterablePart.generateTeams();
        checkEquals(part, "ListList");
    }

    public void testIgnoreAnn() {
        try {
            BIIgnore4Test ignore4Test = new BIIgnore4Test("a", "b");
            generate(ignore4Test, "ignore");
            Object o = BIConstructorUtils.forceConstructObject(Class.forName(BIIgnore4Test.class.getName()));
            BIIgnore4Test result = (BIIgnore4Test) get(o, "ignore");
            assertEquals(result.getA(), (""));
            assertFalse(ComparatorUtils.equals(result.getB(), ("")));

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testIgnoreStaticControlWrite() {
        try {
            BIIgnore4Test ignore4Test = new BIIgnore4Test("a", "b");
            XMLNormalValueWriter.IS_IGNORED_FIELD_USABLE = false;
            XMLNormalValueReader.IS_IGNORED_FIELD_USABLE = false;

            generate(ignore4Test, "IgnoreStaticControlWrite");
            Object o = BIConstructorUtils.forceConstructObject(Class.forName(BIIgnore4Test.class.getName()));
            BIIgnore4Test result = (BIIgnore4Test) get(o, "IgnoreStaticControlWrite");
            assertEquals(result.getA(), ("a"));
            assertEquals(result.getB(), ("b"));

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testIgnoreStaticControlRead() {
        try {
            BIIgnore4Test ignore4Test = new BIIgnore4Test("a", "b");
            XMLNormalValueWriter.IS_IGNORED_FIELD_USABLE = false;
            XMLNormalValueReader.IS_IGNORED_FIELD_USABLE = false;

            generate(ignore4Test, "IgnoreStaticControlWrite");
            Object o = BIConstructorUtils.forceConstructObject(Class.forName(BIIgnore4Test.class.getName()));
            BIIgnore4Test result = (BIIgnore4Test) get(o, "IgnoreStaticControlWrite");
            assertEquals(result.getA(), ("a"));
            assertEquals(result.getB(), ("b"));
            XMLNormalValueReader.IS_IGNORED_FIELD_USABLE = true;
            Object ignoreFieldObj = BIConstructorUtils.forceConstructObject(Class.forName(BIIgnore4Test.class.getName()));

            result = (BIIgnore4Test) get(ignoreFieldObj, "IgnoreStaticControlWrite");
            assertEquals(result.getA(), (""));
            assertEquals(result.getB(), ("b"));

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testInnerClass() {
        try {
            InnerClass4Test innerClass4Test = new InnerClass4Test();
            innerClass4Test.initialInner();
            checkEquals(innerClass4Test, "Inner");
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testFieldDefaultType() {
        BookRack rack = new BookRack();
        Student student = new Student();
        rack.setOwner(student);
        checkEquals(rack, "FieldDefaultType");
    }

    public void testIterable() {
        IterableObj integers = new IterableObj();
        checkEquals(integers, "testIterable");
    }

    public void testArrayContainNull() {
        ArrayPart arrayPart = new ArrayPart();
        Integer[] array = new Integer[3];
        array[0] = 1;
        array[1] = 3;
        array[2] = null;
        arrayPart.setIntegers(array);

        checkEquals(arrayPart, "testGroupValueIndex");
    }

    public void testArrayEmpty() {
        ArrayPart arrayPart = new ArrayPart();
        Integer[] array = new Integer[0];
        arrayPart.setIntegers(array);
        checkEquals(arrayPart, "testGroupValueIndex");
    }

//    public void testOriginalArray() {
//        Integer[] array = new Integer[3];
//        array[0] = 1;
//        array[1] = 3;
//        array[2] = 5;
//
//        checkArrayEquals(array, "testGroupValueIndex",3);
//    }

    public void testGroupValueIndex() {
        int[] array = new int[3];
        array[0] = 1;
        array[1] = 3;
        array[2] = 6;

        GroupValueIndex groupValueIndex = RoaringGroupValueIndex.createGroupValueIndex(array);
        checkEquals(groupValueIndex, "testGroupValueIndex");
    }

    public void testUseAttrSaveValue() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        XMLNormalValueWriter.USE_CONTENT_SAVE_VALUE = false;
        test.value = "abcabc";
        checkEquals(test, "testUseAttrSaveValue");
    }

    public void testUseAttrSaveEmpty() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        XMLNormalValueWriter.USE_CONTENT_SAVE_VALUE = false;
        test.value = "";
        checkEquals(test, "testUseAttrSaveEmpty");
    }

    public void testUseContentSaveValue() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        test.value = "abc\nabc";
        checkEquals(test, "testUseAttrSaveValue");
    }

    public void testUseContentNull() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        test.value = null;
        checkEquals(test, "testUseContentNull");
    }

    public void testUseContentEmpty() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        test.value = "";
        checkEquals(test, "testUseContentEmpty");
    }

    public void testUseInvalidChar() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        StringBuffer sb = new StringBuffer();
        sb.append("abc").append("pn\u0003ô").append("  ").append("t\b");
//        sb.append("abc").append("pn").append("  ").append("t\b");
        char ch = (char) 0x08;
        sb.append(ch).append("\r\n");
        String cont = sb.toString();
        cont.replace("\b", "");
        System.out.printf(cont);
        test.value = cont;
//        checkEquals(test, "testUseInvalidChar");
    }

    public void testUseInvalidValue() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        StringBuffer sb = new StringBuffer();
        sb.append("\u0000");
        sb.append("\u0001");
        sb.append("\u0002");
        sb.append("\u0003");
        sb.append("\u0004");
        sb.append("\u0005");
        sb.append("\u0006");
        sb.append("\u0007");
        sb.append("\u0008");
        sb.append("\u0009");
        sb.append("\u000b");
        sb.append("\u000c");
        sb.append("\u000e");
        sb.append("\u000f");
        sb.append("\u0010");
        sb.append("\u0011");
        sb.append("\u0012");
        sb.append("\u0013");
        sb.append("\u0014");
        sb.append("\u0015");
        sb.append("\u0016");
        sb.append("\u0017");
        sb.append("\u0018");
        sb.append("\u0019");
        sb.append("\u001a");
        sb.append("\u001b");
        sb.append("\u001c");
        sb.append("\u001d");
        sb.append("\u001e");
        sb.append("\u001f");
        test.value = sb.toString();
        checkEquals(test, "testInvalidValue");
    }

    public void testFilterInvalidCode() {
        String content = BIFileUtils.readFile("F:\\work\\BI\\env\\WebReport\\WEB-INF\\resources\\BusinessPackage.xml");
        BIFileUtils.writeFile("F:\\work\\BI\\env\\WebReport\\WEB-INF\\resources\\BusinessPackage.xml", content.replaceAll("[\u0000-\u0008\u000b-\u000c\u000e-\u001f]", ""));
    }


    public void testUUID() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }






    private String[] getInvalidCharArray() {
        String[] invalidString = new String[32];
        invalidString[0x0000] = BIXMLConstant.INVALID_VALUE.U0000;
        invalidString[0x0001] = BIXMLConstant.INVALID_VALUE.U0001;
        invalidString[0x0002] = BIXMLConstant.INVALID_VALUE.U0002;
        invalidString[0x0003] = BIXMLConstant.INVALID_VALUE.U0003;
        invalidString[0x0004] = BIXMLConstant.INVALID_VALUE.U0004;
        invalidString[0x0005] = BIXMLConstant.INVALID_VALUE.U0005;
        invalidString[0x0006] = BIXMLConstant.INVALID_VALUE.U0006;
        invalidString[0x0007] = BIXMLConstant.INVALID_VALUE.U0007;
        invalidString[0x0008] = BIXMLConstant.INVALID_VALUE.U0008;
        invalidString[0x000b] = BIXMLConstant.INVALID_VALUE.U000B;
        invalidString[0x000c] = BIXMLConstant.INVALID_VALUE.U000C;
        invalidString[0x000e] = BIXMLConstant.INVALID_VALUE.U000E;
        invalidString[0x000f] = BIXMLConstant.INVALID_VALUE.U000F;
        invalidString[0x0010] = BIXMLConstant.INVALID_VALUE.U0010;
        invalidString[0x0011] = BIXMLConstant.INVALID_VALUE.U0011;
        invalidString[0x0012] = BIXMLConstant.INVALID_VALUE.U0012;
        invalidString[0x0013] = BIXMLConstant.INVALID_VALUE.U0013;
        invalidString[0x0014] = BIXMLConstant.INVALID_VALUE.U0014;
        invalidString[0x0015] = BIXMLConstant.INVALID_VALUE.U0015;
        invalidString[0x0016] = BIXMLConstant.INVALID_VALUE.U0016;
        invalidString[0x0017] = BIXMLConstant.INVALID_VALUE.U0017;
        invalidString[0x0018] = BIXMLConstant.INVALID_VALUE.U0018;
        invalidString[0x0019] = BIXMLConstant.INVALID_VALUE.U0019;
        invalidString[0x001a] = BIXMLConstant.INVALID_VALUE.U001A;
        invalidString[0x001b] = BIXMLConstant.INVALID_VALUE.U001B;
        invalidString[0x001c] = BIXMLConstant.INVALID_VALUE.U001C;
        invalidString[0x001e] = BIXMLConstant.INVALID_VALUE.U001E;
        invalidString[0x001f] = BIXMLConstant.INVALID_VALUE.U001F;
        return invalidString;
    }

    public void testRegexCostTime() {
        String reg = "[\u0000-\u0008\u000b-\u000c\u000e-\u001f]";

        StringBuffer sb = new StringBuffer();
        sb.append("\u0000");
        sb.append("\u0001");
        sb.append("\u0002");
        sb.append("\u0003");
        sb.append("\u0004");
        sb.append("\u0005");
        sb.append("\u0006");
        sb.append("\u0007");
        sb.append("\u0008");
        sb.append("\u0009");
        sb.append("\u0020");
        sb.append("\u000b");
        sb.append("\u000c");
        sb.append("\u000e");
        sb.append("\u000f");
        sb.append("\u0010");
        sb.append("\u0011");
        sb.append("\u0012");
        sb.append("\u0013");
        sb.append("\u0014");
        sb.append("\u0015");
        sb.append("\u0016");
        sb.append("\u0017");
        sb.append("\u0018");
        sb.append("\u0019");
        sb.append("\u001a");
        sb.append("\u001b");
        sb.append("\u001c");
        sb.append("\u001d");
        sb.append("\u001e");
        sb.append("\u001f");
        String originalValue = sb.toString();
        StringBuilder stringBuilder = new StringBuilder();
        String strCopy = originalValue;
        long startTime = System.currentTimeMillis();


        for (int j = 0; j < 100000; j++) {
            stringBuilder.delete(0, stringBuilder.length());
            strCopy = originalValue;



            char[] originalValueCharArray = strCopy.toCharArray();
            String[] invalidCharValueArray = getInvalidCharArray();

            for (int i = 0; i < originalValueCharArray.length; i++) {
                char c = originalValueCharArray[i];
                if (c <= 0x1f) {
                    if (invalidCharValueArray[c] != null) {
                        stringBuilder.append(invalidCharValueArray[c]);
                    } else {
                        stringBuilder.append(c);
                    }
                } else {
                    stringBuilder.append(c);
                }
            }
        }


        long endTime = System.currentTimeMillis();
        System.out.println("result is: " + strCopy);
        System.out.println("cost time :" + (endTime - startTime) + "ms");
    }

}