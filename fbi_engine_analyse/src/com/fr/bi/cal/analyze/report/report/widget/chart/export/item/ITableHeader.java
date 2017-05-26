package com.fr.bi.cal.analyze.report.report.widget.chart.export.item;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.ITableStyle;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
import com.fr.json.JSONCreator;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableHeader extends JSONCreator {
    boolean isUsed();

    boolean isSum();

    void setSum(boolean sum);

    void setStyles (ITableStyle style);

    void parseJson(JSONObject json) throws JSONException;
}
