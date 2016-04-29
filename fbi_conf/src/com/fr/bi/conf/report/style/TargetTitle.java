package com.fr.bi.conf.report.style;

import com.fr.chart.chartattr.Title;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.StringUtils;

/**
 * Created by sheldon on 14-12-30.
 */
public class TargetTitle implements JSONParser {

    private boolean hasTitle = false;
    private String title = StringUtils.EMPTY;

    public Title createTitle() {
        if (hasTitle) {
            return new Title(title);
        } else {
            return new Title(null);
        }
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        //1 是show, 0 是hidden
        hasTitle = jsonObject.getInt("title_style") == 1;
        title = jsonObject.getString("title_value");
    }
}