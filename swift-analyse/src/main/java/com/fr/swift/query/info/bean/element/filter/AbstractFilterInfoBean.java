package com.fr.swift.query.info.bean.element.filter;

/**
 * @author yee
 * @date 2018/6/22
 */
public abstract class AbstractFilterInfoBean implements FilterInfoBean {

    protected BeanType beanType;

    public AbstractFilterInfoBean(BeanType beanType) {
        this.beanType = beanType;
    }


    @Override
    public BeanType getBeanType() {
        return beanType;
    }
}
