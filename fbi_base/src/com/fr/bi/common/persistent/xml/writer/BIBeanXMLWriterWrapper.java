package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.common.persistent.BIBeanWriterWrapper;
import com.fr.bi.stable.utils.program.BIFieldUtils;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Connery on 2015/12/29.
 */
public class BIBeanXMLWriterWrapper extends BIBeanWriterWrapper {

    private String UUID;
    private Boolean isSampleModel;


    /**
     * bean是不是对象的属性。
     * 属性对象的tag默认是属性名称
     * 如果不是，那么tag不能为null或者“”
     * 否则没有办法解析的。二维数组就是典型的例子
     * 二维的对象如果没有tag，那么最后生成的就是一维
     * 数组的形式了
     * 例如：Person.name那么tag默认是name
     */
    private Boolean isProperty = false;
    private String tag;
    private Boolean isTagAvailable = false;

    public Boolean getTagAvailable() {
        return isTagAvailable;
    }

    public void setTagAvailable(Boolean tagAvailable) {
        isTagAvailable = tagAvailable;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getSampleModel() {
        return isSampleModel;
    }

    public void setSampleModel(Boolean sampleModel) {
        isSampleModel = sampleModel;
    }

    public Boolean getProperty() {
        return isProperty;
    }

    public void setProperty(Boolean property) {
        isProperty = property;
    }

    public BIBeanXMLWriterWrapper(Object bean) {
        this(bean, null);
    }

    public BIBeanXMLWriterWrapper(Object bean, String tag) {
        super(bean, bean.getClass());

        if (tag != null && !ComparatorUtils.equals(tag, "")) {
            this.tag = tag;
            this.isProperty = true;
        } else {
            this.isProperty = false;
        }
        this.isSampleModel = false;
        UUID = java.util.UUID.randomUUID().toString();
    }

    public String getSimpleClassName() {
        return beanClass.getSimpleName();
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }


    public XMLValueWriter generateWriter(Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        if (BIFieldUtils.isBasicType(beanClass)) {
            return new XMLBasicValueWriter(this, disposedBeans);
        } else if (BIFieldUtils.isIterableType(beanClass)) {
            return new XMLIterableValueWriter(this, disposedBeans);
        } else if (BIFieldUtils.isMapType(beanClass)) {
            return new XMLMapValueWriter(this, disposedBeans);
        } else if (BIFieldUtils.isArrayType(beanClass)) {
            return new XMLArrayValueWriter(this, disposedBeans);
        } else {
            return new XMLNormalValueWriter(this, disposedBeans);
        }
    }

    public boolean isBasicBean() {
        return BIFieldUtils.isBasicType(bean.getClass());
    }
}