package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

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

    public void setFilterIndex(GroupValueIndex gvi) {
        filterIndex = gvi;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetricGroupInfo info = (MetricGroupInfo) o;

        if (!ComparatorUtils.equals(rows, info.rows)) {
            return false;
        }
        if (!ComparatorUtils.equals(filterIndex, info.filterIndex)) {
            return false;
        }
        if (!ComparatorUtils.equals(metric, info.metric)) {
            return false;
        }
        return ComparatorUtils.equals(summaryList, info.summaryList);
    }

}
