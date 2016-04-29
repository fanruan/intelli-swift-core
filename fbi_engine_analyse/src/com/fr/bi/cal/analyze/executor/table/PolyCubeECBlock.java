package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.general.ComparatorUtils;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.AbstractPolyECBlock;
import com.fr.report.report.TemplateReport;

import java.util.Map;

public class PolyCubeECBlock extends AbstractPolyECBlock {

    /**
     *
     */
    private static final long serialVersionUID = -9043778293830147331L;

    private String sessionID;
    private TableWidget widget;
    private int page;

    public PolyCubeECBlock(TableWidget widget, String sessionID, int page) {
        super();
        this.widget = widget;
        this.sessionID = sessionID;
        this.page = page;
    }


    @Override
    protected SheetExecutor createSheetExecutor(TemplateReport report, AbstractPolyECBlock block, Map parameterMap, BlockSequenceExecutor blockSequenceExecutor) {
        return new CubeExecutor(report, widget, sessionID, block, parameterMap, page);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && o instanceof PolyCubeECBlock
                && ComparatorUtils.equals(widget, ((PolyCubeECBlock) o).widget);
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