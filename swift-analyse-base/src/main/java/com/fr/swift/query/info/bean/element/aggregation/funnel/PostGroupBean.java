package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class PostGroupBean {

    @JsonProperty
    private int funnelIndex;
    @JsonProperty
    private String column;
    @JsonProperty
    private List<double[]> rangePairs = new ArrayList<double[]>();

    public PostGroupBean() {
    }

    public PostGroupBean(int funnelIndex, String column, List<double[]> rangePairs) {
        this.funnelIndex = funnelIndex;
        this.column = column;
        this.rangePairs = rangePairs;
    }

    public int getFunnelIndex() {
        return funnelIndex;
    }

    public void setFunnelIndex(int funnelIndex) {
        this.funnelIndex = funnelIndex;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<double[]> getRangePairs() {
        return rangePairs;
    }

    public void setRangePairs(List<double[]> rangePairs) {
        this.rangePairs = rangePairs;
    }
}
