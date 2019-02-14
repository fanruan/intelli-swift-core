package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelMedianInfoBean extends AbstractPostQueryInfoBean {

    {
        type = PostQueryType.FUNNEL_MEDIAN;
    }

    @Override
    public PostQueryType getType() {
        return type;
    }
}
