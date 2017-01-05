package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2017/1/3.
 */
public class MetricMergeResult {
    protected final static MetricMergeResult NULL = new MetricMergeResult(null, null);

    private Object data;
    private String showValue;
    private GroupValueIndex[] gvis;

    public MetricMergeResult(Object data, GroupValueIndex[] gvis) {
        this.data = data;
        this.gvis = gvis;
        initShowValue();
    }

    private void initShowValue() {
        showValue = data == null ? null : data.toString();
    }

    public Object getData() {
        return data;
    }

    public String getShowValue() {
        return showValue;
    }

    public GroupValueIndex[] getGvis() {
        return gvis;
    }
}
