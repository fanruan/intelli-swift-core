package com.fr.swift.adaptor.widget.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.EmptyAlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.classification.net.NeuralNetworkBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.classification.tree.DecisionTreeBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.analysis.vistor.DMBeanVisitor;
import com.fr.swift.adaptor.widget.datamining.kmeans.KmeansGroupTableAdapter;
import com.fr.swift.adaptor.widget.datamining.timeseries.TimeSeriesGroupTableAdapter;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Jonas on 2018/5/14.
 */
public class GroupTableToDMResultVisitor implements DMBeanVisitor<SwiftResultSet> {
    private NodeResultSet result;
    private TableWidget widget;
    private GroupQueryInfoImpl info;
    private DMErrorWrap errorWrap;

    public GroupTableToDMResultVisitor(NodeResultSet result, TableWidget widget, GroupQueryInfoImpl info, DMErrorWrap errorWrap) {
        this.result = result;
        this.widget = widget;
        this.info = info;
        this.errorWrap = errorWrap;
    }

    @Override
    public SwiftResultSet visit(HoltWintersBean bean) {
        TimeSeriesGroupTableAdapter adapter = new TimeSeriesGroupTableAdapter(bean, widget, result, info, errorWrap);
        return adapter.getResult();
    }

    @Override
    public SwiftResultSet visit(KmeansBean bean) throws Exception {
        KmeansGroupTableAdapter adapter = new KmeansGroupTableAdapter(bean, widget, result, info, errorWrap);
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
