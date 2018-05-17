package com.fr.swift.adaptor.widget.datamining.timeseries;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Jonas on 2018/5/15.
 */
public class TimeSeriesCrossTableAdapter implements SwiftAlgorithmResultAdapter<HoltWintersBean, CrossTableWidget, NodeResultSet, XGroupQueryInfo> {

    private HoltWintersBean bean;
    private CrossTableWidget widget;
    private NodeResultSet result;
    private XGroupQueryInfo info;

    @Override
    public SwiftResultSet getResult(HoltWintersBean bean, CrossTableWidget widget, NodeResultSet result, XGroupQueryInfo info) {
        this.bean = bean;
        this.widget = widget;
        this.result = result;
        this.info = info;
        if (info.getColDimensionInfo().getDimensions().length == 0 || info.getDimensionInfo().getDimensions().length == 0) {
            return handleGroupTable();
        } else {
            // 行列表头都不为空
            return handleCrossTable();
        }
    }

    private SwiftResultSet handleGroupTable() {
        TimeSeriesGroupTableAdapter groupTableAdapter = new TimeSeriesGroupTableAdapter();
        return groupTableAdapter.getResult(bean, widget, result, info);
    }

    private SwiftResultSet handleCrossTable() {
        return result;
    }
}
