package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.util.Util;

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FunnelEventBean that = (FunnelEventBean) o;

        return Util.isEqualCollection(that.steps, that.steps);
    }

    @Override
    public int hashCode() {
        return steps != null ? steps.hashCode() : 0;
    }
}
