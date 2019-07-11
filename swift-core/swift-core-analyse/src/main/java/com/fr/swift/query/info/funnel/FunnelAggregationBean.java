package com.fr.swift.query.info.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.funnel.group.post.PostGroupBean;
import com.fr.swift.query.info.funnel.group.time.TimeGroup;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelAggregationBean extends FunnelPathsAggregationBean {
    @JsonProperty("timeGroup")
    private TimeGroup timeGroup;
    @JsonProperty("postGroup")
    private PostGroupBean postGroup;
    @JsonProperty("calculateTime")
    private boolean calculateTime;

    {
        type = AggregatorType.FUNNEL;
    }


    public void setTimeGroup(TimeGroup timeGroup) {
        this.timeGroup = timeGroup;
    }

    public TimeGroup getTimeGroup() {
        return timeGroup;
    }

    public PostGroupBean getPostGroup() {
        return postGroup;
    }

    public void setPostGroup(PostGroupBean postGroup) {
        this.postGroup = postGroup;
    }

    public boolean isCalculateTime() {
        return calculateTime;
    }

    public void setCalculateTime(boolean calculateTime) {
        this.calculateTime = calculateTime;
    }

}
