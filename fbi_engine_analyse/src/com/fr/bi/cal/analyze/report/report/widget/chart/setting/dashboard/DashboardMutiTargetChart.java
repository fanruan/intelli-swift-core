package com.fr.bi.cal.analyze.report.report.widget.chart.setting.dashboard;

import com.fr.base.TableData;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.report.report.widget.ChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.setting.pie.PIEMutiTargetChart;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.DashBoardDefaultStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.DashBoardStyle;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.chart.chartdata.MeterTableDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DashboardMutiTargetChart extends PIEMutiTargetChart {

    private static final String COLUMNNAME = "column";
    private static final String VALUENAME = "value";

    @Override
    protected int getChartType() {
        return BIExcutorConstant.CHART.DASHBOARD;
    }

    /**
     * 转成json
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("style")) {
            style = new DashBoardStyle();
            style.parseJSON(jo.getJSONObject("style"));
        } else {
            style = new DashBoardDefaultStyle();
        }
    }

    @Override
    protected TopDefinition crateChartMoreValueDefinition(BITarget[] summarys) {
        MeterTableDefinition tableDataDef = new MeterTableDefinition();
        tableDataDef.setValue(VALUENAME);
        tableDataDef.setName(COLUMNNAME);
        return tableDataDef;
    }

    /**
     * 创建tableData
     * @param dimensions  维度
     * @param targets  目标
     * @param session   当前用户
     * @param cc  图表集合
     * @return tableData对象
     */
	@Override
	public TableData createTableData(ChartWidget widget, BIDimension[] dimensions,
			BISummaryTarget[] targets,
			BISession session, BaseChartCollection cc) {
        BISummaryTarget[] summary = getUsedTargets(targets);
		Node node = getCubeNode(widget, dimensions, targets,  session);
		EmbeddedTableData resData = new EmbeddedTableData();
		resData.addColumn(COLUMNNAME, String.class);
		resData.addColumn(VALUENAME, Double.class);
		Node n = node;
		while (n.getFirstChild() != null) {
			n = n.getFirstChild();
		}
		int slen = summary.length;
		TargetGettingKey[] key = new TargetGettingKey[slen];
		for(int i = 0; i < slen; i++){
			key[i] = new TargetGettingKey(summary[i].createSummaryKey(session.getLoader()), summary[i].getValue());
			List<Object> rowList = new ArrayList<Object>();
			rowList.add(summary[i].getValue());
			rowList.add(n.getSummaryValue(key[i]));
			resData.addRow(rowList);
		}
		return resData;
	}

}