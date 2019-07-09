package com.fr.swift.query.info.group.post;

import com.fr.swift.query.funnel.TimeWindowBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * This class created on 2018/12/14
 *
 * @author Lucifer
 * @description
 */
public class FunnelPostQueryInfo implements PostQueryInfo {

    private TimeWindowBean timeWindowBean;
    private PostQueryType type;

    public FunnelPostQueryInfo(TimeWindowBean timeWindowBean) {
        this.timeWindowBean = timeWindowBean;
    }

    @Override
    public PostQueryType getType() {
        return type;
    }

    public void setType(PostQueryType type) {
        this.type = type;
    }

    public TimeWindowBean getTimeWindowBean() {
        return timeWindowBean;
    }
}
