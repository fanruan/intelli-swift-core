package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2017/1/3.
 */
public class MetricMergeResult extends Node {
    protected final static MetricMergeResult NULL = new MetricMergeResult(null, 0, null);

    private GroupValueIndex[] gvis;


    public MetricMergeResult(Object data, int sumLen, GroupValueIndex[] gvis) {
        super(data, sumLen);
        this.gvis = gvis;
    }

    public MetricMergeResult(Comparator c, Object data, int sumLen, GroupValueIndex[] gvis) {
        super(c, data, sumLen);
        this.gvis = gvis;
    }

    public void setGvis(GroupValueIndex[] gvis) {
        this.gvis = gvis;
    }

    public GroupValueIndex[] getGvis() {
        return gvis;
    }

    public void clearGvis(){
        gvis = null;
    }
}
