package com.fr.bi.common.persistent.writer;


import junit.framework.TestCase;


/**
 * Created by Connery on 2016/1/2.
 */
public class XMLReaderTest extends TestCase {
//    public void testReadOneNormal() {
//        try {
//            File var3 = new File("D:\\FineBI\\重构\\code\\WebReport\\WEB-INF\\resources\\UserTableRelationManager.xml");
//            StableUtils.makesureFileExist(var3);
//            BIUserTableRelationManager part = new BIUserTableRelationManager();
//            XMLPersistentReader object = new XMLPersistentReader(new HashMap<String, BIBeanReaderWrapper>(), new BIBeanReaderWrapper(part));
//            XMLTools.readInputStreamXML(object, new FileInputStream(var3));
//            System.out.println(part);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void testReadTwoCollection() {
//        IterablePart part = new IterablePart();
//        Object o = get(part, "IteratorPersonTwo");
//        System.out.printf(o.toString());
//    }
//    public void testReadTwoCollectionConnect() {
//        IterablePart part = new IterablePart();
//        Object o = get(part, "IteratorPersonTwoConnect");
//        System.out.printf(o.toString());
//    }
//
//    private Object get(Object object, String name) {
//        try {
//            File var3 = new File("D:\\temp\\" + name + ".xml");
//            StableUtils.makesureFileExist(var3);
//            XMLPersistentReader reader = new XMLPersistentReader(new HashMap<String, BIBeanReaderWrapper>(), new BIBeanReaderWrapper(object));
//            XMLTools.readInputStreamXML(reader, new FileInputStream(var3));
//            return object;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return object;
//    }
}