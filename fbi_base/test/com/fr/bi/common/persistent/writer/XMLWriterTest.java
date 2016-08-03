package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLNormalValueReader;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLNormalValueWriter;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.bi.common.world.BookRack;
import com.fr.bi.common.world.people.Student;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.xml.XMLTools;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLWriterTest extends TestCase {

    public void testArrayNull() {
        try {
            ArrayPart partNull = ArrayPart.generateNull();
            checkEquals(partNull, "arrayNull");
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

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
            BILogger.getLogger().error(e.getMessage(), e);
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
            File var3 = new File("D:\\temp\\" + name + ".xml");
            StableUtils.makesureFileExist(var3);
            XMLTools.writeOutputStreamXML(object, new FileOutputStream(var3));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
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
            BILogger.getLogger().error(e.getMessage(), e);

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
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

    private Object get(Object object, String name) {
        try {
            File var3 = new File("D:\\temp\\" + name + ".xml");
            StableUtils.makesureFileExist(var3);
            XMLPersistentReader reader = new XMLPersistentReader(new HashMap<String, BIBeanXMLReaderWrapper>(), new BIBeanXMLReaderWrapper(object));
            XMLTools.readInputStreamXML(reader, new FileInputStream(var3));
            return object;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return object;
    }

    public void testReadTwoMapBasic() {
        MapPart part = new MapPart();
        Object o = get(part, "MapBasicTwo");
        BILogger.getLogger().info(o.toString());
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
            BILogger.getLogger().error(e.getMessage(), e);
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
            BILogger.getLogger().error(e.getMessage(), e);
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
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testInnerClass() {
        try {
            InnerClass4Test innerClass4Test = new InnerClass4Test();
            innerClass4Test.initialInner();
            checkEquals(innerClass4Test, "Inner");
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
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
        Integer[] array = new Integer[3];
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

    public void testUseContentSaveValue() {
        NormalString4SpecialChar test = new NormalString4SpecialChar();
        test.value = "abc\nabc";
        checkEquals(test, "testUseAttrSaveValue");
    }
}