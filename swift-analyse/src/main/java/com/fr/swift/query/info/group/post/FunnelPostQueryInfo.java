package com.fr.swift.query.info.group.post;

import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * This class created on 2018/12/14
 *
 * @author Lucifer
 * @description
 */
public class FunnelPostQueryInfo implements PostQueryInfo {

    private FunnelQueryBean queryBean;

    public FunnelPostQueryInfo(FunnelQueryBean queryBean) {
        this.queryBean = queryBean;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.FUNNEL_MEDIAN;
    }

    public FunnelQueryBean getQueryBean() {
        return queryBean;
    }
}
