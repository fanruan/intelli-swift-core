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
public class TimeSeriesCrossTableAdapter implements SwiftAlgorithmResultAdapter<HoltWintersBean, CrossTableWidget, XNodeMergeResultSet, XGroupQueryInfo> {
    @Override
    public SwiftResultSet getResult(HoltWintersBean bean, CrossTableWidget widget, XNodeMergeResultSet result, XGroupQueryInfo info) throws Exception {
        return result;
    }
}
