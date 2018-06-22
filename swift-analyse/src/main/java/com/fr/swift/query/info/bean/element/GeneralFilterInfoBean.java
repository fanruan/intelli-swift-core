package com.fr.swift.query.info.bean.element;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class GeneralFilterInfoBean extends AbstractFilterInfoBean {
    List<FilterInfoBean> children;
    int type;

    public GeneralFilterInfoBean() {
        super(BeanType.GENERAL);
    }

    public List<FilterInfoBean> getChildren() {
        return children;
    }

    public void setChildren(List<FilterInfoBean> children) {
        this.children = children;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
