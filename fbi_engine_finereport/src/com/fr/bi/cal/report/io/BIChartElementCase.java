package com.fr.bi.cal.report.io;

import com.fr.base.chart.BaseChartPainter;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.DefaultPageCellElement;
import com.fr.report.cellcase.DefaultCellCase;
import com.fr.report.elementcase.AbstractElementCase;
import com.fr.report.poly.ResultChartBlock;


/**
 * @author:ben Administrator
 * @time: 2012 2012-9-6
 * @description:TODO 这边太恶心了，为了适用excelexport，整体考虑block的export
 */
public class BIChartElementCase extends AbstractElementCase {
    /**
     *
     */
    private static final long serialVersionUID = -7680744782189819423L;

    public BIChartElementCase(ResultChartBlock chartBlock) {
        this.cellcase = new BIChartBlockCellCase(chartBlock.getChartPainter());
        this.setColumnWidth(0, chartBlock.getEffectiveWidth());
        this.setRowHeight(0, chartBlock.getEffectiveHeight());
//		this.chartBlock = chartBlock;
    }

    @Override
    protected CellElement createDefaultCellElementCase() {
        return new DefaultPageCellElement();
    }

    private class BIChartBlockCellCase extends DefaultCellCase {

        /**
         *
         */
        private static final long serialVersionUID = -2364768191905536281L;

        public BIChartBlockCellCase(BaseChartPainter chartPainter) {
            this.add(new DefaultPageCellElement(0, 0, chartPainter), true);
        }

    }
}