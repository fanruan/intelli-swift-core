package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.post.PostGroupBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.time.TimeGroup;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelAggregationBean extends FunnelPathsAggregationBean {
    @JsonProperty("timeGroup")
    private TimeGroup timeGroup;
    @JsonProperty("postGroup")
    private PostGroupBean postGroup;


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

    @Override
    public AggregatorType getType() {
        return AggregatorType.FUNNEL;
    }

    @Override
    public FilterInfoBean getFilter() {
        FilterInfoBean bean = super.getFilter();
        AndFilterBean filterBean = new AndFilterBean();
        List<FilterInfoBean> list = new ArrayList<FilterInfoBean>();
        list.add(bean);
        FilterInfoBean filter = timeGroup.filter();
        if (filter instanceof DetailFilterInfoBean) {
            ((DetailFilterInfoBean) filter).setColumn(getColumns().getTimestamp());
        }
        list.add(filter);
        filterBean.setFilterValue(list);
        return filterBean;
    }
}
