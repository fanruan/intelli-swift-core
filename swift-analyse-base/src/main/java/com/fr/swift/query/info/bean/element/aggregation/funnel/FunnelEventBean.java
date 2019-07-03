package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelEventBean {
    @JsonProperty("name")
    private String name;
    @JsonProperty("steps")
    private List<String> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "FunnelEventBean{" +
                "name='" + name + '\'' +
                ", steps=" + steps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunnelEventBean)) return false;

        FunnelEventBean that = (FunnelEventBean) o;

        return steps != null ? steps.equals(that.steps) : that.steps == null;
    }

    @Override
    public int hashCode() {
        return steps != null ? steps.hashCode() : 0;
    }
}
