package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.cal.analyze.cal.chart.PolyCubeChartBlock;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSetting;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyChartBlock;
import com.fr.report.poly.TemplateBlock;

/**
 * BI图表控件
 *
 * @author Daniel-pc
 */
public class ChartWidget extends BISummaryWidget {

    private BIChartSetting data;

    public ChartWidget(BIChartSetting data) {
        this.data = data;
    }

    /**
     * 将JSONObject属性转换成JAVA属性
     *
     * @param jo jsonobject对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (data != null && jo.has("view")) {
            data.parseJSON(jo.getJSONObject("view"));
        }
        if (data != null && jo.has("name")) {
            data.setWidgetName(jo.getString("name"));
        }
    }

    public BIChartSetting getChartSetting() {
        return data;
    }

    /**
     * 创建BLOCK的方法
     */
    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        PolyChartBlock block = new PolyCubeChartBlock(this, session);
        BIDimension[] dimensions = this.getDimensions();
        BITarget[] targets = this.getTargets();
        block.setChartCollection(data.createChartCollection(dimensions, targets, this.getWidgetName()));
        return block;
    }


    @Override
    public BIDimension[] getViewDimensions() {
        return data.getUsedDimensions(getDimensions());
    }

    @Override
    public BITarget[] getViewTargets() {
        return data.getUsedTargets(getTargets());
    }

    @Override
    public int isOrder() {
        return 0;
    }

    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws JSONException {
		if(session == null){
			return null;
		}
		BIChartSetting data = getChartSetting();
		BIDimension[] dimensions = getDimensions();
		BISummaryTarget[] targets = getTargets();
        JSONObject jo = new JSONObject();
        jo.put("data", data.getCubeNode(this, dimensions, targets, (BISession) session).toJSONObject(getDimensions(), getTargetsKey(), -1));
        return jo;
    }

    @Override
    public int getType() {
        return BIReportConstant.WIDGET.NONE;
    }
}