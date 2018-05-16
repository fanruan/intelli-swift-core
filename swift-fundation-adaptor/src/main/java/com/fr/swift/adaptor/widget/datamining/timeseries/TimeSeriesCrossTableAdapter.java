package com.fr.swift.adaptor.widget.datamining.timeseries;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Jonas on 2018/5/15.
 */
public class TimeSeriesCrossTableAdapter implements SwiftAlgorithmResultAdapter<HoltWintersBean, CrossTableWidget, SwiftResultSet, XGroupQueryInfo> {

    private HoltWintersBean bean;
    private CrossTableWidget widget;
    private SwiftResultSet result;
    private XGroupQueryInfo info;

    @Override
    public SwiftResultSet getResult(HoltWintersBean bean, CrossTableWidget widget, SwiftResultSet result, XGroupQueryInfo info) throws Exception {
        this.bean = bean;
        this.widget = widget;
        this.result = result;
        this.info = info;
        if (info.getColDimensionInfo().getDimensions().length == 0) {
            // 列表头为空
            return handleVertical();
        } else if (info.getDimensionInfo().getDimensions().length == 0) {
            // 行表头为空
            return handleHorizontal();
        } else {
            // 行列表头都不为空
            return handleCrossTable();
        }
    }

    private SwiftResultSet handleVertical() {
        return result;
    }

    private SwiftResultSet handleHorizontal() {
        return result;
    }

    private SwiftResultSet handleCrossTable() {
        return result;
    }
}
