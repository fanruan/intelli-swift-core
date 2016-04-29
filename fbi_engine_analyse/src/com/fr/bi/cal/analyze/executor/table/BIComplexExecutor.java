package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;

import java.awt.*;
import java.util.Map;

/**
 * Created by sheldon on 14-9-1.
 */
public abstract class BIComplexExecutor<T> extends BIAbstractExecutor<T> {

    //from 1~ max
    protected BISummaryTarget[] usedSumTarget;
    protected BISummaryTarget[] allSumTarget;
    protected BIDimension[] allDimensions;
    protected TableWidget widget;
    protected ComplexExpander complexExpander;
    protected String widgetName;

    protected BIComplexExecutor(TableWidget widget, Paging page, BISession session, ComplexExpander expander) {
        super(widget, page, session);
        this.widget = widget;
        usedSumTarget = widget.getViewTargets();
        allSumTarget = widget.getTargets();
        allDimensions = widget.getDimensions();
        this.complexExpander = expander;
    }

    /**
     * 获取nodes 复杂列表获取的是一个nodes
     *
     * @return 获取的nodes
     */
    public abstract Map<Integer, T> getCubeNodes() throws InterruptedException;

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    public abstract int getNodesTotalLength(Node[] nodes);

    /**
     * 获取node的个数
     */
    public abstract int getNodesTotalLength(Node[] nodes, ComplexExpander expander, Integer[] ints);


    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }

    //给null格子加空的，让冻结的时候砍掉
    protected void geneEmptyCells(CBCell[][] cbcells) {
    }
}