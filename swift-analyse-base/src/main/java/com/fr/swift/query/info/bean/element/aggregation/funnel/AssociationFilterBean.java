package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class AssociationFilterBean {

    @JsonProperty
    private String column;
    @JsonProperty
    private List<Integer> funnelIndexes;

    public AssociationFilterBean() {
    }

    public AssociationFilterBean(String column, List<Integer> funnelIndexes) {
        this.column = column;
        this.funnelIndexes = funnelIndexes;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<Integer> getFunnelIndexes() {
        return funnelIndexes;
    }

    public void setFunnelIndexes(List<Integer> funnelIndexes) {
        this.funnelIndexes = funnelIndexes;
    }
}
