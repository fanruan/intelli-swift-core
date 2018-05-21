package com.fr.swift.adaptor.widget.datamining.kmeans;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

/**
 * @author qingj
 */
public class KmeansCrossTableAdapter implements SwiftAlgorithmResultAdapter<KmeansBean, CrossTableWidget, NodeResultSet, XGroupQueryInfo> {


    @Override
    public SwiftResultSet getResult(KmeansBean bean, CrossTableWidget widget, NodeResultSet result, XGroupQueryInfo info) throws Exception {
        return null;
    }
}