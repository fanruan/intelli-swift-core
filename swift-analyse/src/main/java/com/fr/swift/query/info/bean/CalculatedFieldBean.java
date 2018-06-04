package com.fr.swift.query.info.bean;

import com.fr.swift.query.info.element.target.cal.CalTargetType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/2.
 */
public class CalculatedFieldBean {

    @JsonProperty
    private CalTargetType type;
    @JsonProperty
    private String name;    // 计算指标的名称
    @JsonProperty
    private List<String> parameter;

    public CalTargetType getType() {
        return type;
    }

    public void setType(CalTargetType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameter() {
        return parameter;
    }

    public void setParameter(List<String> parameter) {
        this.parameter = parameter;
    }
}
