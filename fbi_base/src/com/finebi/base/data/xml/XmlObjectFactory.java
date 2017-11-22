package com.finebi.base.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 对象构造类
 */
public interface XmlObjectFactory {

    /**
     * 返回生成对象
     * @param attributes
     * @return
     * @throws Exception
     */
    Object getObject(Attributes attributes) throws Exception;

    /**
     * 生成标记属性
     * @param o
     * @param attr
     */
    void createAttr(Object o, AttributesImpl attr);
}
