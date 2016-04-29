package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Iterator;

/**
 * Created by User on 2016/4/25.
 */
public class MultiChartWidget extends TableWidget {
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");
            JSONArray ja = new JSONArray();
            Iterator it = vjo.keys();
            while (it.hasNext()){
                String region = it.next().toString();
                if(ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
                        ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)){
                    continue;
                }
                JSONArray tmp =  vjo.getJSONArray(region);
                for(int j = 0; j < tmp.length(); j++){
                    ja.put(tmp.getString(j));
                }
            }
            vjo.remove(BIReportConstant.REGION.TARGET2);
            vjo.remove(BIReportConstant.REGION.TARGET3);
            vjo.put(BIReportConstant.REGION.TARGET1, ja);
        }
        super.parseJSON(jo, userId);
    }
}
