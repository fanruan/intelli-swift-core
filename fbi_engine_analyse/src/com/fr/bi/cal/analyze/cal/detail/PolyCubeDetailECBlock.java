package com.fr.bi.cal.analyze.cal.detail;

import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.general.ComparatorUtils;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.AbstractPolyECBlock;
import com.fr.report.report.TemplateReport;

import java.util.Map;

public class PolyCubeDetailECBlock extends AbstractPolyECBlock {

    /**
     *
     */
    private static final long serialVersionUID = -7295150813914967051L;
    private BISession session;
    private BIDetailWidget widget;
    private int page;

    public PolyCubeDetailECBlock(BIDetailWidget widget, BISession session, int page) {
        super();
        this.widget = widget;
        this.session = session;
        this.page = page;
    }

    @Override
    protected SheetExecutor createSheetExecutor(TemplateReport report,
                                                AbstractPolyECBlock block, Map parameterMap,
                                                BlockSequenceExecutor executor) {
        return new CubeDetailExecutor(report, widget, session, block, parameterMap, page);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && o instanceof PolyCubeDetailECBlock
                && ComparatorUtils.equals(widget, ((PolyCubeDetailECBlock) o).widget);
    }

    @Override
    public int[] getVerticalLine() {
        return new int[0];
    }

    @Override
    public int[] getHorizontalLine() {
        return new int[0];
    }
}