package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
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
//        generate(part, "MapPersonTwo");
    }

    public void testMapTwo() {
        MapPart part = MapPart.generateTwo();
//        generate(part, "MapTwo");
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
//        generate(part, "NormalOnePerson");
        checkEquals(part, "NormalOnePerson");
    }

    private void checkEquals(Object obj, String name) {
        try {
            generate(obj, name);
            Object o = BIConstructorUtils.forceConstructObject(Class.forName(obj.getClass().getName()));
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

}