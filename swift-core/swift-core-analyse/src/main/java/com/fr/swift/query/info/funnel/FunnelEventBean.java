package com.fr.swift.query.info.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

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
    @JsonProperty("filter")
    private FilterInfoBean filter;

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

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
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
        if (!(o instanceof FunnelEventBean)) {
            return false;
        }

        FunnelEventBean that = (FunnelEventBean) o;

        if (steps != null ? !steps.equals(that.steps) : that.steps != null) {
            return false;
        }
        return filter != null ? filter.equals(that.filter) : that.filter == null;
    }

    @Override
    public int hashCode() {
        int result = steps != null ? steps.hashCode() : 0;
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
}
