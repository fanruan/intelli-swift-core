package com.fr.bi.cal.analyze.cal.chart;

import com.fr.bi.cal.analyze.report.report.widget.ChartWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.PolyChartBlock;
import com.fr.report.report.TemplateReport;

import java.util.Map;

public class PolyCubeChartBlock extends PolyChartBlock {

    /**
     *
     */
    private static final long serialVersionUID = -4978104545167751898L;

    private ChartWidget widget;
    private BISessionProvider session;

    public PolyCubeChartBlock(ChartWidget widget, BISessionProvider session) {
        this.widget = widget;
        this.session = session;
    }

    @Override
    public SheetExecutor createExecutor(TemplateReport report, Map parameterMap, BlockSequenceExecutor bExecuter) {
        // richer:由于图表类型的聚合块有单独的处理方式，所有这里返回null
        PolyCubeChartBlock block;
        try {
            block = (PolyCubeChartBlock) this.clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (parameterMap == null) {
            parameterMap = java.util.Collections.EMPTY_MAP;
        }
        return new CubeChartExecutor(report, block, widget, session, parameterMap, bExecuter);
    }

}