package com.fr.bi.cal.analyze.report.report.widget.imp;

import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetUtils;
import com.fr.bi.conf.report.conf.BIWidgetConfUtils;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * 这个类后面可能需要删了，合并到TableWidget
 * Created by User on 2016/4/25.
 */
public class VanChartWidget extends TableWidget {


    //createJSON的时候经过处理得到前台图表需要的plotOptions
    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

        // 如果是实时数据
        if(BIWidgetConfUtils.needOpenBigDateModel(getWidgetConf())){
            setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.BIGDATACHART);
        }
        JSONObject data = super.createDataJSON(session, req).getJSONObject("data");

        return BIWidgetUtils.generateResult4View(getWidgetConf(), data);
    }

}