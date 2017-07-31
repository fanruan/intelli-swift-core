package com.fr.bi.cal.analyze.report.report.widget.imp;

import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetUtils;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetConfUtils;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 这个类后面可能需要删了，合并到TableWidget
 * Created by User on 2016/4/25.
 */
public class VanChartWidget extends TableWidget {


    //createJSON的时候经过处理得到前台图表需要的plotOptions
    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

        // 如果是实时数据
        if(BIWidgetConfUtils.needUseBigDataOperator(getWidgetConf())){
            setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.BIGDATACHART);
        }
        JSONObject data = super.createDataJSON(session, req).getJSONObject("data");

        updateConfigForPhone(req, getWidgetConf());

        return BIWidgetUtils.generateResult4View(getWidgetConf(), data);
    }

    private static final String[] legend = {"legendStyle"};
    private static final String[] label = {"dataLabelSetting", "textStyle"};
    private static final String[] tooltip = {"tooltipStyle", "textStyle"};
    private static final String[] cate = {"catLabelStyle", "textStyle"};
    private static final String[] leftY = {"leftYLabelStyle", "textStyle"};
    private static final String[] rightY = {"rightYLabelStyle", "textStyle"};
    private static final String[] rightY2 = {"rightY2LabelStyle", "textStyle"};

    private static Map<String[], Integer> checkMap = new HashMap<String[], Integer>();
    static {
        checkMap.put(legend, 28);
        checkMap.put(label, 24);
        checkMap.put(tooltip, 24);
        checkMap.put(cate, 24);
        checkMap.put(leftY, 24);
        checkMap.put(rightY, 24);
        checkMap.put(rightY2, 24);
    }

    private static void updateConfigForPhone(HttpServletRequest req, BIWidgetConf conf) throws JSONException{
        if(WebUtils.getDevice(req).isPhone()){
            JSONObject settings = conf.getWidgetSettings().getDetailSettings();

            Iterator<String[]> iterator = checkMap.keySet().iterator();
            while (iterator.hasNext()){
                String[] keys = iterator.next();

                JSONObject o = settings;
                for(String key : keys){
                    if(o != null) {
                        o = o.optJSONObject(key);
                    }
                }

                if(o != null && o.optInt("fontSize") > checkMap.get(keys)){
                    o.put("fontSize", checkMap.get(keys));
                }
            }
        }
    }

}