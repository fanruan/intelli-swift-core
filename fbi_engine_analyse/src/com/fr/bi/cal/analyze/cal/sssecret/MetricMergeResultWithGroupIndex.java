package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2017/3/24.
 */
public class MetricMergeResultWithGroupIndex extends MetricMergeResult {
    private int[] groupIndex;
    public MetricMergeResultWithGroupIndex(Comparator c, Object data, GroupValueIndex[] gvis, int[] groupIndex) {
        super(c, data, gvis);
        this.groupIndex = groupIndex;
    }

    public int[] getGroupIndex() {
        return groupIndex;
    }
}
