package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.info.bean.element.filter.AbstractFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralFilterInfoBean extends AbstractFilterInfoBean {
    @JsonProperty
    private List<FilterInfoBean> children;
    @JsonProperty
    private int type;

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
