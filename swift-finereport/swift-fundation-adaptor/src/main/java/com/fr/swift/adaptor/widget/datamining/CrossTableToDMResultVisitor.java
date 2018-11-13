package com.fr.swift.adaptor.widget.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.EmptyAlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.classification.net.NeuralNetworkBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.classification.tree.DecisionTreeBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.analysis.vistor.DMBeanVisitor;
import com.fr.swift.adaptor.widget.datamining.kmeans.KmeansCrossTableAdapter;
import com.fr.swift.adaptor.widget.datamining.timeseries.TimeSeriesCrossTableAdapter;
import com.fr.swift.query.info.group.XGroupQueryInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

/**
 * 这个地方有点特殊，如果只有行维度或者只有列维度，返回一个分组表结构。
 * Created by Jonas on 2018/5/14.
 */
public class CrossTableToDMResultVisitor implements DMBeanVisitor<SwiftResultSet> {
    private NodeResultSet result;
    private CrossTableWidget widget;
    private XGroupQueryInfo info;
    private DMErrorWrap dmErrorWrap;

    public CrossTableToDMResultVisitor(NodeResultSet result, CrossTableWidget widget, XGroupQueryInfo info, DMErrorWrap dmErrorWrap) {
        this.result = result;
        this.widget = widget;
        this.info = info;
        this.dmErrorWrap = dmErrorWrap;
    }

    @Override
    public SwiftResultSet visit(HoltWintersBean bean) throws Exception {
        TimeSeriesCrossTableAdapter adapter = new TimeSeriesCrossTableAdapter(bean, widget, result, info, dmErrorWrap);
        return adapter.getResult();
    }

    @Override
    public SwiftResultSet visit(KmeansBean bean) throws Exception {
        KmeansCrossTableAdapter adapter = new KmeansCrossTableAdapter(bean, widget, result, info, dmErrorWrap);
        return adapter.getResult();
    }

    @Override
    public SwiftResultSet visit(NeuralNetworkBean bean) {
        return result;
    }

    @Override
    public SwiftResultSet visit(DecisionTreeBean bean) {
        return result;
    }

    @Override
    public SwiftResultSet visit(EmptyAlgorithmBean bean) {
        return result;
    }
}
