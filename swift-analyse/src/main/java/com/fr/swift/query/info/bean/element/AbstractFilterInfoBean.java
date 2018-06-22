package com.fr.swift.query.info.bean.element;

/**
 * @author yee
 * @date 2018/6/22
 */
public class AbstractFilterInfoBean implements FilterInfoBean {

    protected BeanType beanType;

    public AbstractFilterInfoBean(BeanType beanType) {
        this.beanType = beanType;
    }


    @Override
    public BeanType getBeanType() {
        return null;
    }
}
