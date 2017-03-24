package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.general.Inter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BITableExecutor<T> extends BIAbstractExecutor<T> {

    protected BISummaryTarget[] usedSumTarget;
    protected BISummaryTarget[] allSumTarget;
    protected BIDimension[] allDimensions;
    protected BIDimension[] usedDimensions;
    protected TableWidget widget;
    protected CrossExpander expander;

    protected BITableExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session);
        this.widget = widget;
        usedSumTarget = widget.getViewTargets();
        allSumTarget = widget.getTargets();
        allDimensions = widget.getDimensions();
        usedDimensions = widget.getViewDimensions();
        this.expander = expander;
//        this.expander = CrossExpander.ALL_EXPANDER;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }
}