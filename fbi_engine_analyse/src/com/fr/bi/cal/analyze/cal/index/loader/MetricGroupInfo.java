package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/12/26.
 */
public class MetricGroupInfo {
    private DimensionCalculator[] rows;
    private GroupValueIndex filterIndex;
    private BusinessTable metric;
    private List<TargetAndKey> summaryList = new ArrayList<TargetAndKey>();

    public MetricGroupInfo(DimensionCalculator[] rows, GroupValueIndex filterIndex, BusinessTable metric) {
        this.rows = rows;
        this.filterIndex = filterIndex;
        this.metric = metric;
    }

    public DimensionCalculator[] getRows() {
        return rows;
    }

    public GroupValueIndex getFilterIndex() {
        return filterIndex;
    }

    public BusinessTable getMetric() {
        return metric;
    }

    public List<TargetAndKey> getSummaryList() {
        return summaryList;
    }

    public void addTargetAndKey(TargetAndKey calculator) {
        summaryList.add(calculator);
    }
}
