package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.dashboard.widget.control.number.SingleSliderWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.string.ListLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.MonthControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.QuarterControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.YearControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.service.engine.excutor.EngineWidgetExecutorManager;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.structure.result.BIResult;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.finebi.conf.structure.result.control.number.BISingleSliderResult;
import com.finebi.conf.structure.result.control.string.BIListLabelResult;
import com.finebi.conf.structure.result.control.time.BIMonthControlResult;
import com.finebi.conf.structure.result.control.time.BIQuarterResult;
import com.finebi.conf.structure.result.control.time.BIYearControlResult;
import com.finebi.conf.structure.result.control.tree.BITreeResult;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BIGroupNode;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class EngineWidgetExecutorManagerImpl implements EngineWidgetExecutorManager {
    @Override
    public BIGroupNode visit(TableWidget tableWidget) {
        return null;
    }

    @Override
    public BICrossNode visit(CrossTableWidget tableWidget) throws Exception {
        return null;
    }

    @Override
    public BIDetailTableResult visit(DetailWidget detailWidget) {
        return null;
    }

    @Override
    public BIStringDetailResult visit(StringControlWidget detailWidget) {
        return null;
    }

    @Override
    public BISingleSliderResult visit(SingleSliderWidget detailWidget) throws Exception {
        return null;
    }

    @Override
    public BIYearControlResult visit(YearControlWidget detailWidget) throws Exception {
        return null;
    }

    @Override
    public BIQuarterResult visit(QuarterControlWidget detailWidget) throws Exception {
        return null;
    }

    @Override
    public BIMonthControlResult visit(MonthControlWidget detailWidget) throws Exception {
        return null;
    }

    @Override
    public BITreeResult visit(TreeWidget detailWidget) throws Exception {
        return null;
    }

    @Override
    public BIResult visit(TreeLabelWidget labelWidget) throws Exception {
        return null;
    }

    @Override
    public BIResult visit(TreeListWidget labelWidget) throws Exception {
        return null;
    }

    @Override
    public BIListLabelResult visit(ListLabelWidget listLabelWidget) throws Exception {
        return null;
    }

    @Override
    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String fieldId) {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}