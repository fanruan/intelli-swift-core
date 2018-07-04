package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.post.PostQueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class CalculatedFieldQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private List<CalculatedFieldBean> calculatedFieldBeans;

    {
        type = PostQueryType.CAL_FIELD;
    }

    public List<CalculatedFieldBean> getCalculatedFieldBeans() {
        return calculatedFieldBeans;
    }

    public void setCalculatedFieldBeans(List<CalculatedFieldBean> calculatedFieldBeans) {
        this.calculatedFieldBeans = calculatedFieldBeans;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.CAL_FIELD;
    }
}
