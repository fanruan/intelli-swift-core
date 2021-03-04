package com.fr.swift.cloud.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.query.sort.SortType;

/**
 * @author Lyon
 * @date 2018/6/2
 */
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private String name;      // TODO: 2018/7/4 结果排序还是字典排序要区分原始字段名和客户端定义的字段转义名

    public SortBean(SortType type, String name) {
        this.type = type;
        this.name = name;
    }

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
