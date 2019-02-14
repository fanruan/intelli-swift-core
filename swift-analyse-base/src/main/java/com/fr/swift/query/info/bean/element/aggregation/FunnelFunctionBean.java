package com.fr.swift.query.info.bean.element.aggregation;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.aggregation.funnel.AssociationFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.DayFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.PostGroupBean;

import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelFunctionBean {

    @JsonProperty
    private int timeWindow;
    @JsonProperty
    private DayFilterBean dayFilter;
    @JsonProperty
    private ParameterColumnsBean columns;
    @JsonProperty
    private List<String> funnelEvents;
    @JsonProperty
    private AssociationFilterBean associatedFilter;
    @JsonProperty
    private PostGroupBean postGroup;

    public FunnelFunctionBean() {
    }

    public FunnelFunctionBean(int timeWindow, DayFilterBean dayFilter, ParameterColumnsBean columns, List<String> funnelEvents) {
        this.timeWindow = timeWindow;
        this.dayFilter = dayFilter;
        this.columns = columns;
        this.funnelEvents = funnelEvents;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    public DayFilterBean getDayFilter() {
        return dayFilter;
    }

    public void setDayFilter(DayFilterBean dayFilter) {
        this.dayFilter = dayFilter;
    }

    public ParameterColumnsBean getColumns() {
        return columns;
    }

    public void setColumns(ParameterColumnsBean columns) {
        this.columns = columns;
    }

    public List<String> getFunnelEvents() {
        return funnelEvents;
    }

    public void setFunnelEvents(List<String> funnelEvents) {
        this.funnelEvents = funnelEvents;
    }

    public AssociationFilterBean getAssociatedFilter() {
        return associatedFilter;
    }

    public void setAssociatedFilter(AssociationFilterBean associatedFilter) {
        this.associatedFilter = associatedFilter;
    }

    public PostGroupBean getPostGroup() {
        return postGroup;
    }

    public void setPostGroup(PostGroupBean postGroup) {
        this.postGroup = postGroup;
    }

    public static Builder builder(int timeWindow, DayFilterBean dayFilter,
                                  ParameterColumnsBean columns, List<String> funnelEvents) {
        return new Builder(timeWindow, dayFilter, columns, funnelEvents);
    }

    public static class Builder {
        private FunnelFunctionBean bean;

        public Builder(int timeWindow, DayFilterBean dayFilter, ParameterColumnsBean columns, List<String> funnelEvents) {
            this.bean = new FunnelFunctionBean(timeWindow, dayFilter, columns, funnelEvents);
        }

        public Builder setAssociatedFilter(AssociationFilterBean associatedFilter) {
            bean.setAssociatedFilter(associatedFilter);
            return this;
        }

        public Builder setPostGroup(PostGroupBean postGroup) {
            bean.setPostGroup(postGroup);
            return this;
        }

        public FunnelFunctionBean build() {
            return bean;
        }
    }
}
