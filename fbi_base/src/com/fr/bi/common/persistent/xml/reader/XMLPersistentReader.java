package com.fr.bi.common.persistent.xml.reader;

import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2015/12/29.
 */
public class XMLPersistentReader implements XMLReadable {
    protected BIBeanXMLReaderWrapper beanWrapper;
    protected Map<String, BIBeanXMLReaderWrapper> generatedBean;


    public XMLPersistentReader(Map<String, BIBeanXMLReaderWrapper> generatedBean, BIBeanXMLReaderWrapper beanWrapper) {
        this.generatedBean = generatedBean;
        this.beanWrapper = beanWrapper;
    }

    public BIBeanXMLReaderWrapper getBeanWrapper() {
        return beanWrapper;
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {
        beanWrapper.generateReader(generatedBean).readValue(xmLableReader);
    }

//    private void setSampleValue(String fieldName, Class fieldClass, XMLableReader xmLableReader) throws
//            IntrospectionException, IllegalAccessException, InvocationTargetException {
//        String str = xmLableReader.getAttrAsString("value", "null");
//        Object value = BITypeUtils.stringConvert2BasicType(fieldClass, str);
//        if (value != null) {
//            setValue(fieldName, value);
//        }
//    }
//
//    private void setValue(String fieldName, Object value) throws
//            IntrospectionException, IllegalAccessException, InvocationTargetException {
//        beanWrapper.setValue(fieldName, value);
//    }
//
//    private void setLeftValue(String fieldName, Class fieldClass, XMLableReader xmLableReader) throws
//            IntrospectionException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        String className = xmLableReader.getAttrAsString("class", "BI_NULL");
//        String uuid = xmLableReader.getAttrAsString("uuid", "BI_NULL");
//        if (!className.equals("BI_NULL") && !uuid.equals("BI_NULL")) {
//            Object fieldValue = getObject(uuid, className);
//            XMLPersistentReader reader = new XMLPersistentReader(generatedBean, new BIBeanWriterWrapper(fieldValue));
//            reader.readXML(xmLableReader);
//            setValue(fieldName, reader.getBeanWrapper().getBean());
//        }
//
//    }
//
//
//
//
//    private void setCollectionValue(String fieldName, Class fieldClass, XMLableReader xmLableReader) throws
//            IntrospectionException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        String className = xmLableReader.getAttrAsString("class", "BI_NULL");
//        String uuid = xmLableReader.getAttrAsString("uuid", "BI_NULL");
//        if (!className.equals("BI_NULL") && !uuid.equals("BI_NULL")) {
//            Collection fieldValue = (Collection) getObject(uuid, className);
//            CollectionXMLReader collectionXMLReader = new CollectionXMLReader(fieldValue);
//            collectionXMLReader.readXML(xmLableReader);
//            setValue(fieldName, collectionXMLReader.result);
//        }
//    }
//
//    protected Class loadClass(String className) throws ClassNotFoundException {
//        try {
//            return Class.forName(className);
//        } catch (ClassNotFoundException e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        }
//        return null;
//    }
//
//    protected Object constructObject(Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        return BIConstructorUtils.constructObject(clazz);
//    }
//
//    private Boolean isSampleType(Class clazz) {
//        return BIFieldUtils.isBasicType(clazz);
//    }
//
//    private Boolean isLeftType(Class clazz) {
//        return !isSampleType(clazz) && !isCollectionType(clazz);
//    }
//
//    private boolean isCollectionType(Class clazz) {
//        return BIFieldUtils.isCollectionType(clazz);
//    }
//
//    private class CollectionXMLReader implements XMLReadable {
//        Collection result;
//
//        public CollectionXMLReader(Collection result) {
//            this.result = result;
//        }
//
//        @Override
//        public void readXML(XMLableReader xmLableReader) {
//            try {
//                xmLableReader.readXMLObject(new XMLReadable() {
//                    @Override
//                    public void readXML(XMLableReader xmLableReader) {
//                        if (xmLableReader.isChildNode()) {
//                            if (ComparatorUtils.equals(xmLableReader.getTagName(), "item")) {
//                                try {
//                                    Class valueClass = loadClass(xmLableReader.getAttrAsString("class", "java.lang.Object"));
//                                    if (ComparatorUtils.equals(xmLableReader.getAttrAsString("sample", "no"), "yes")) {
//
//                                        String value = xmLableReader.getAttrAsString("value", "BI_NULL");
//                                        result.add(BITypeUtils.stringConvert2BasicType(valueClass, value));
//
//                                    } else {
//                                        String className = xmLableReader.getAttrAsString("class", "BI_NULL");
//                                        String uuid = xmLableReader.getAttrAsString("uuid", "BI_NULL");
//                                        if (!className.equals("BI_NULL") && !uuid.equals("BI_NULL")) {
//                                            Object object = getObject(uuid, className);
//                                            XMLPersistentReader reader = new XMLPersistentReader(generatedBean, new BIBeanWriterWrapper(object));
//                                            reader.readXML(xmLableReader);
//                                            result.add(reader.getBeanWrapper().getBean());
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//                                    BILogger.getLogger().error(e.getMessage(), e);
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                });
//            } catch (Exception e) {
//            }
//        }
//    }
}