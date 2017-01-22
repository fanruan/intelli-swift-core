package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.condition;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.objectcondition.AbstractXYZFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * empty condition
 * Created by AstronautOO7 on 2016/9/19.
 */
public class EmptyFilter extends AbstractXYZFilter {

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        return new JSONArray();
    }
}
