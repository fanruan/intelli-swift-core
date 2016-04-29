package com.fr.bi.cal.analyze.cal.chart;

import com.fr.base.TableData;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.report.report.widget.ChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSetting;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.chart.chartdata.TableDataDefinition;
import com.fr.report.block.ResultBlock;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.block.ChartBlockExecutor;
import com.fr.report.poly.PolyChartBlock;
import com.fr.report.report.TemplateReport;

import java.util.Map;

/**
 * 立方 图表 执行计算.
 *
 * @author kunsnat E-mail:kunsnat@gmail.com
 * @version 创建时间：2013-3-27 下午02:45:02
 */
public class CubeChartExecutor extends ChartBlockExecutor {

    private static final long serialVersionUID = -8188108711134007280L;
    private ChartWidget widget;
    private BISession session;

    public CubeChartExecutor(TemplateReport report, PolyChartBlock tplBlock,
                             ChartWidget widget, BISessionProvider session, Map parameterMap, BlockSequenceExecutor bsExecuter) {
        super(report, tplBlock, parameterMap, bsExecuter);
        this.widget = widget;
        this.session = (BISession)session;
    }

    /**
     * 计算聚合结果
     *
     * @param actor     锚
     * @param startTime 开始时间
     * @return 结果
     */
    @Override
    public ResultBlock execute4Poly(com.fr.report.stable.fun.Actor actor, long startTime) {
        if (session == null) {
            return null;
        }
        BIChartSetting data = widget.getChartSetting();
        BaseChartCollection cc = tplBlock.getChartCollection();
		if(data != null) {
            TableData tableData = data.createTableData(widget, widget.getDimensions(), widget.getTargets(),session, cc);
            for(int i = 0, length = cc.getChartCount(); i < length; i++) {
                TableDataDefinition tdf = (TableDataDefinition)cc.getChartWithIndex(i).getFilterDefinition();
                if(tdf != null ){
                    tdf.setTableData(tableData);
                }
			}
		}
        return super.execute4Poly(actor, startTime);
    }

}