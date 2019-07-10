package com.fr.swift.query.info.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.funnel.filter.TimeFilterInfo;
import com.fr.swift.query.query.funnel.TimeWindowBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelPathsAggregationBean implements AggregationBean {

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
    @JsonProperty
    protected AggregatorType type;
    @JsonProperty
    protected FilterInfoBean filter;
    @JsonProperty
    private Object[] params;
    @JsonProperty
    private String alias;
    /**
     * 原始表中的字段名
     */
    @JsonProperty
    private String column;

    {
        type = AggregatorType.FUNNEL_PATHS;
    }

    @Override
    public AggregatorType getType() {
        return type;
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
        if (null == filter) {
            AndFilterBean andFilterBean = new AndFilterBean();
            List<FilterInfoBean> filters = new ArrayList<FilterInfoBean>();
            if (null != timeFilter) {
                filters.add(timeFilter.createFilter());
            }
            Set<String> eventFilterValue = new HashSet<String>();
            for (FunnelEventBean event : events) {
                eventFilterValue.addAll(event.getSteps());
            }
            filters.add(new InFilterBean(getColumn(), eventFilterValue.toArray()));
            andFilterBean.setFilterValue(filters);
            return andFilterBean;
        }
        return filter;
    }
}
