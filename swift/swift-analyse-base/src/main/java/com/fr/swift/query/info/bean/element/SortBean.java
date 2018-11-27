package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.sort.SortType;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private String name;      // TODO: 2018/7/4 结果排序还是字典排序要区分原始字段名和客户端定义的字段转义名

    public SortBean() {
    }

    public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
