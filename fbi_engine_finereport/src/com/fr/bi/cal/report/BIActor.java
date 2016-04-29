/**
 *
 */
package com.fr.bi.cal.report;

import com.fr.base.chart.BaseChartPainter;
import com.fr.bi.cal.report.report.poly.BIPolyAnalyChartBlock;
import com.fr.report.poly.ResultChartBlock;
import com.fr.stable.ActorConstants;
import com.fr.stable.ViewActor;


public class BIActor extends ViewActor {

    @Override
    public String panelType() {
        return ActorConstants.TYPE_BI;
    }


    @Override
    public ResultChartBlock getChartBlock4Ploy(BaseChartPainter chartPainter) {
        return new BIPolyAnalyChartBlock(chartPainter);
    }
}