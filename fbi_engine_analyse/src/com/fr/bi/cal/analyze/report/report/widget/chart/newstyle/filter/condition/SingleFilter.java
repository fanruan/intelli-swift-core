package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.condition;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.FilterValueFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class SingleFilter implements IFilter {

    private IFilter filterValue;

    public SingleFilter(JSONObject jo) throws JSONException{
        filterValue = FilterValueFactory.parseFilter(jo);
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        return this.filterValue.getFilterResult(array);
    }
}
 