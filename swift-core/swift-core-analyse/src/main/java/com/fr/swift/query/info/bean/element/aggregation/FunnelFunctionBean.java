package com.fr.swift.query.info.bean.element.aggregation;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.funnel.FunnelAssociationBean;
import com.fr.swift.query.info.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.funnel.filter.DayFilterInfo;
import com.fr.swift.query.info.funnel.group.post.PostGroupBean;

import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelFunctionBean {

    @JsonProperty
    private int timeWindow;
    @JsonProperty
    private DayFilterInfo dayFilter;
    @JsonProperty
    private ParameterColumnsBean columns;
    @JsonProperty
    private List<String> funnelEvents;
    @JsonProperty
    private FunnelAssociationBean associatedFilter;
    @JsonProperty
    private PostGroupBean postGroup;

    public FunnelFunctionBean() {
    }

    public FunnelFunctionBean(int timeWindow, DayFilterInfo dayFilter, ParameterColumnsBean columns, List<String> funnelEvents) {
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

    public static Builder builder(int timeWindow, DayFilterInfo dayFilter,
                                  ParameterColumnsBean columns, List<String> funnelEvents) {
        return new Builder(timeWindow, dayFilter, columns, funnelEvents);
    }

    public DayFilterInfo getDayFilter() {
        return dayFilter;
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

    public void setDayFilter(DayFilterInfo dayFilter) {
        this.dayFilter = dayFilter;
    }

    public FunnelAssociationBean getAssociatedFilter() {
        return associatedFilter;
    }

    public PostGroupBean getPostGroup() {
        return postGroup;
    }

    public void setPostGroup(PostGroupBean postGroup) {
        this.postGroup = postGroup;
    }

    public void setAssociatedFilter(FunnelAssociationBean associatedFilter) {
        this.associatedFilter = associatedFilter;
    }

    public static class Builder {
        private FunnelFunctionBean bean;

        public Builder(int timeWindow, DayFilterInfo dayFilter, ParameterColumnsBean columns, List<String> funnelEvents) {
            this.bean = new FunnelFunctionBean(timeWindow, dayFilter, columns, funnelEvents);
        }

        public Builder setAssociatedFilter(FunnelAssociationBean associatedFilter) {
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
