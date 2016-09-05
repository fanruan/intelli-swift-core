package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.ChinesePinyinComparator;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new ChinesePinyinComparator());
            for(String region : sorted){
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

    @Override
    public int getType() {
        return BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART;
    }
}
