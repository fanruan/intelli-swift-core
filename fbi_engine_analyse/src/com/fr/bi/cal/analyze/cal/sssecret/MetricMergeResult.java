package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2017/1/3.
 */
public class MetricMergeResult extends Node {
    protected final static MetricMergeResult NULL = new MetricMergeResult(null, null);

    private GroupValueIndex[] gvis;


    public MetricMergeResult(Object data, GroupValueIndex[] gvis) {
        super(data);
        this.gvis = gvis;
    }

    public MetricMergeResult(Comparator c, Object data, GroupValueIndex[] gvis) {
        super(c, data);
        this.gvis = gvis;
    }

    public GroupValueIndex[] getGvis() {
        return gvis;
    }
}
