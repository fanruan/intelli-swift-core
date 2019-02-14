package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * Created by Lyon on 2018/6/3.
 */
public class CalculatedFieldQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private CalculatedFieldBean calField;

    {
        type = PostQueryType.CAL_FIELD;
    }

    public CalculatedFieldBean getCalField() {
        return calField;
    }

    public void setCalField(CalculatedFieldBean calField) {
        this.calField = calField;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.CAL_FIELD;
    }
}
