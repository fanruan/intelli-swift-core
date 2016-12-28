package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/12/26.
 */
public class NoneMetricRootDimensionGroup extends RootDimensionGroup {
    private TargetFilter filter;
    public NoneMetricRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, NodeExpander expander, BISession session, boolean useRealData, TargetFilter filter) {
        super(metricGroupInfoList, expander, session, useRealData);
        this.filter = filter;
    }
}
