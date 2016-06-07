package com.fr.bi.common.persistent.xml.reader;

import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public abstract class XMLValueReader {
    protected BIBeanXMLReaderWrapper beanWrapper;
    protected Map<String, BIBeanXMLReaderWrapper> generatedBean;

    public XMLValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        this.beanWrapper = beanWrapper;
        this.generatedBean = generatedBean;
    }

    /**
     * 获得空对象，或者已有对象。
     * 如果获得已有对象，不能直接给对象赋值。
     * 仍然需要通过解析xml。因为是流式解析，
     * 没有办法跳过某一部分的xml
     *
     * @param component
     * @param className
     * @return
     * @throws IntrospectionException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected BIBeanXMLReaderWrapper getObjectWrapper(String component, String className) throws
            IntrospectionException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        String uuid = getUUID(component);
        BIBeanXMLReaderWrapper fieldValueWrapper = getGeneratedWrapper(uuid);

        if (fieldValueWrapper == null) {
            /**
             * 字段的实际类型
             */
            Class fieldTrueClass = loadClass(className);
            Object fieldObject;
            if (BITypeUtils.isArrayType(fieldTrueClass)) {
                int size = getSize(component);
                fieldObject = constructArrayObject(fieldTrueClass.getComponentType(), size);
            } else {
                fieldObject = constructObject(fieldTrueClass);
            }
            BIBeanXMLReaderWrapper readerWrapper = new BIBeanXMLReaderWrapper(fieldObject);
            if (!isUUIDEmpty(uuid)) {
                addGeneratedWrapper(uuid, readerWrapper);
            }
            return readerWrapper;
        } else {
            fieldValueWrapper.setDisposed(true);
        }
        return fieldValueWrapper;
    }

    private String getUUID(String component) {
        if (component.contains("#")) {
            return component.split("#")[0];
        } else {
            return component;
        }
    }

    private int getSize(String component) {

        if (component.contains("#")) {
            return Integer.valueOf(component.split("#")[1]);
        } else {
            return 0;
        }
    }

    /**
     * @param uuid
     * @param fieldValue
     * @return
     * @throws IntrospectionException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected BIBeanXMLReaderWrapper getObjectWrapper(String uuid, Object fieldValue) throws
            IntrospectionException, ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        BIBeanXMLReaderWrapper fieldValueWrapper = getGeneratedWrapper(uuid);

        if (fieldValueWrapper == null) {

            BIBeanXMLReaderWrapper readerWrapper = new BIBeanXMLReaderWrapper(fieldValue);
            if (!isUUIDEmpty(uuid)) {
                addGeneratedWrapper(uuid, readerWrapper);
            }
            return readerWrapper;
        } else {
            fieldValueWrapper.setDisposed(true);
        }
        return fieldValueWrapper;
    }

    protected Class loadClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    protected Object constructObject(Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructObject(clazz, 0);
    }

    protected Object constructObject(Class clazz, int size) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (BITypeUtils.isAutoBoxType(clazz)) {
            return BIConstructerHelper.getBoxValue(clazz);
        } else if (BITypeUtils.isArrayType(clazz)) {
            return constructArrayObject(clazz, size);
        }
        return BIConstructorUtils.forceConstructObject(clazz);
    }

    protected Object constructArrayObject(Class clazz, int size) {
        return BIConstructorUtils.constructArrayObject(clazz, size);
    }

    private BIBeanXMLReaderWrapper getGeneratedWrapper(String uuid) {
        if (!isUUIDEmpty(uuid)) {
            return generatedBean.get(uuid);
        } else {
            return null;
        }
    }

    private boolean isUUIDEmpty(String uuid) {
        return uuid == null || ComparatorUtils.equals(uuid, "null");
    }

    private void addGeneratedWrapper(String uuid, BIBeanXMLReaderWrapper wrapper) {
        generatedBean.put(uuid, wrapper);
    }

    public void readValue(XMLableReader xmLableReader) {
        try {
            xmLableReader.readXMLObject(new XMLReadable() {
                @Override
                public void readXML(XMLableReader xmLableReader) {
                    if (xmLableReader.isChildNode()) {
//                        try {
//                            String fieldName = xmLableReader.getTagName();
//                            beanWrapper.generateReader(generatedBean).readValue(xmLableReader);
//                        } catch (Exception e) {
//                            BILogger.getLogger().error(e.getMessage(), e);
//                        }
                        readerContent(xmLableReader);
                    } else {
                        try {

                            if (beanWrapper == null) {
                                String component = xmLableReader.getAttrAsString(BIXMLTag.APPEND_INFO, "null");
                                String clazz = xmLableReader.getAttrAsString("class", "null");

                                beanWrapper = new BIBeanXMLReaderWrapper(getObjectWrapper(component, clazz));
                            }

                        } catch (Exception e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        }

                    }
                }
            });
//            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    protected abstract void readerContent(XMLableReader xmLableReader);
}