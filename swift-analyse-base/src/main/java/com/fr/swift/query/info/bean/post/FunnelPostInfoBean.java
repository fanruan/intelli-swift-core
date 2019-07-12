package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 *
 * @author lyon
 * @date 2018/12/28
 */
public class FunnelPostInfoBean extends AbstractPostQueryInfoBean {

    private FunnelPostInfoBean() {
    }

    public FunnelPostInfoBean(PostQueryType type) {
        this.type = type;
    }

    @Override
    public PostQueryType getType() {
        return type;
    }
}
