package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.time.TimeGroup;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelAggregationBean extends FunnelPathsAggregationBean {
    @JsonProperty("timeGroup")
    private TimeGroup timeGroup;


    public TimeGroup getTimeGroup() {
        return timeGroup;
    }

    public void setTimeGroup(TimeGroup timeGroup) {
        this.timeGroup = timeGroup;
    }

    @Override
    public AggregatorType getType() {
        return AggregatorType.FUNNEL;
    }
}
