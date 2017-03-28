package com.fr.bi.cal.analyze.report.report.widget.chart.filter;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class FilterValueFactory {

    public static IFilter parseFilter(JSONObject filter) throws JSONException {
        IFilter filterValue = null;
        if (filter.has("filterType")) {
            int type = filter.getInt("filterType");
            switch (type) {

            }
        }
        return filterValue;
    }
}
