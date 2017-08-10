package com.fr.bi.cal.analyze.report.report;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetUtils;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetConfUtils;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * 这个类后面可能需要删了，合并到TableWidget
 * Created by User on 2016/4/25.
 */
public class VanChartWidget extends TableWidget {

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");
            Iterator it = vjo.keys();
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.parseInt(o1) - Integer.parseInt(o2);
                }
            });

            //通过改变view来控制生成的表结构，分组表or交叉表or复杂表
            JSONObject settings = jo.optJSONObject("settings");
            int type = jo.optInt("type");
            switch (type){
                case BIReportConstant.WIDGET.DOT:
                    changeDotView(sorted, vjo, settings);
                    break;
                case BIReportConstant.WIDGET.LINE_MAP:
                    changeLineMapView(sorted, vjo);
                    break;
                case BIReportConstant.WIDGET.RECT_TREE:
                    changeTreeMapView(sorted, vjo);
                    break;
            }
        }

        super.parseJSON(jo, userId);
    }

    //点图的分类系列全部放到10000，因为要生成只有行表头的复杂表。
    private void changeDotView(List<String> sorted, JSONObject vjo, JSONObject settings) throws JSONException{
        JSONArray ja = JSONArray.create();
        JSONArray seriesIDs = JSONArray.create();
        JSONArray categoryIDs = JSONArray.create();

        int seriesRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION2);

        for (String region : sorted) {

            if (Integer.parseInt(region) > seriesRegion) {
                continue;
            }

            JSONArray tmp = vjo.getJSONArray(region);

            for (int j = 0; j < tmp.length(); j++) {
                String key = tmp.getString(j);
                ja.put(key);

                if(Integer.parseInt(region) == seriesRegion){
                    seriesIDs.put(key);
                }else{
                    categoryIDs.put(key);
                }
            }

            vjo.remove(region);
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, ja);
        settings.put("seriesIDs", seriesIDs);
        settings.put("categoryIDs", categoryIDs);
    }

    // 流向地图的from10000、fromlng10001、fromlat10002、to20000、tolng20001、tolat20002 全部放到10000
    // 因为想要分组表。
    private void changeLineMapView(List<String> sorted, JSONObject vjo) throws JSONException{
        JSONArray ja = JSONArray.create();

        int target1 = Integer.parseInt(BIReportConstant.REGION.TARGET1);

        for (String region : sorted) {

            if (Integer.parseInt(region) >= target1) {
                continue;
            }

            JSONArray tmp = vjo.getJSONArray(region);

            for (int j = 0; j < tmp.length(); j++) {
                String key = tmp.getString(j);
                ja.put(key);
            }

            vjo.remove(region);
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, ja);
    }

    private void changeTreeMapView(List<String> sorted, JSONObject vjo) throws JSONException{
        JSONArray firstRegionArray = JSONArray.create();
        JSONArray secondRegionArray = JSONArray.create();

        int firstRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION2);
        int secondRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION1);

        for (String region : sorted) {

            if (Integer.parseInt(region) == firstRegion) {
                firstRegionArray = vjo.optJSONArray(region);
                vjo.remove(region);
            }

            if (Integer.parseInt(region) == secondRegion) {
                secondRegionArray = vjo.optJSONArray(region);
                vjo.remove(region);
            }
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, firstRegionArray);
        vjo.put(BIReportConstant.REGION.DIMENSION2, secondRegionArray);
    }


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