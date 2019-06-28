package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.funnel.TimeWindowBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.filter.TimeFilterInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelPathsAggregationBean extends MetricBean {

    @JsonProperty("columns")
    protected ParameterColumnsBean columns;
    @JsonProperty("events")
    private List<FunnelEventBean> events;
    @JsonProperty("associations")
    private List<FunnelAssociationBean> associations;
    @JsonProperty("timeWindow")
    private TimeWindowBean timeWindow;
    @JsonProperty("timeFilter")
    private TimeFilterInfo timeFilter;

    @Override
    public AggregatorType getType() {
        return AggregatorType.FUNNEL_PATHS;
    }

    public List<FunnelEventBean> getEvents() {
        return events;
    }

    public void setEvents(List<FunnelEventBean> events) {
        this.events = events;
    }

    public List<FunnelAssociationBean> getAssociations() {
        return associations;
    }

    public void setAssociations(List<FunnelAssociationBean> associations) {
        this.associations = associations;
    }

    public TimeWindowBean getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(TimeWindowBean timeWindow) {
        this.timeWindow = timeWindow;
    }

    public TimeFilterInfo getTimeFilter() {
        return timeFilter;
    }

    public void setTimeFilter(TimeFilterInfo timeFilter) {
        this.timeFilter = timeFilter;
    }

    public ParameterColumnsBean getColumns() {
        return columns;
    }

    public void setColumns(ParameterColumnsBean columns) {
        this.columns = columns;
    }

    @Override
    public FilterInfoBean getFilter() {
        AndFilterBean andFilterBean = new AndFilterBean();
        List<FilterInfoBean> filters = new ArrayList<FilterInfoBean>();
        if (null != timeFilter) {
            try {
                filters.add(timeFilter.createFilter());
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("TimeFilter create FilterInfoBean error, Ignore", e);
            }
        }
        Set<String> eventFilterValue = new HashSet<String>();
        for (FunnelEventBean event : events) {
            eventFilterValue.addAll(event.getSteps());
        }
        filters.add(new InFilterBean(getColumn(), eventFilterValue.toArray()));
        andFilterBean.setFilterValue(filters);
        return andFilterBean;
    }
}
