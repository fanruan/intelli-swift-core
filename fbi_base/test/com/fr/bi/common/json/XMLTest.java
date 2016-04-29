package com.fr.bi.common.json;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.bi.common.world.Book;
import com.fr.bi.common.world.people.Person;
import com.fr.bi.common.world.people.Student;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.StableUtils;
import com.fr.stable.xml.XMLTools;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Connery on 2015/12/29.
 */
public class XMLTest extends TestCase {
    public void testXML() {
        try {
            Book test = new Book();
            test.setBookName("hh");
            test.setPrice(2.3f);
            Person owner = new Person();
            owner.setName("Connery");
            test.setOwner(owner);

            Student student = BIJSONObjectTest.getStudent();
            XMLPersistentWriter object = new XMLPersistentWriter(new BIBeanXMLWriterWrapper(student));
            File var3 = new File("D:\\temp\\testXML.xml");
            StableUtils.makesureFileExist(var3);

            XMLTools.writeOutputStreamXML(object, new FileOutputStream(var3));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

    public void testXMLRead() {
        try {

            File var3 = new File("D:\\temp\\testXML.xml");
            StableUtils.makesureFileExist(var3);
            Book test = new Book();
            Student student = new Student();
            Person owner = new Person();
            XMLPersistentReader object = new XMLPersistentReader(new HashMap<String, BIBeanXMLReaderWrapper>(), new BIBeanXMLReaderWrapper(student));
            XMLTools.readInputStreamXML(object, new FileInputStream(var3));
            Student result = (Student) object.getBeanWrapper().getBean();
//            System.out.println("");
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

}