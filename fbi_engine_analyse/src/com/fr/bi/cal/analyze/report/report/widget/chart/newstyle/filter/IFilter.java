package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public interface IFilter {
    public JSONArray getFilterResult(JSONArray array) throws JSONException;

}
