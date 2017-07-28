package com.fr.bi.cal.analyze.report.report.widget.chart.export.item;

import com.fr.bi.conf.report.style.table.ITableStyle;
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

    String getText();

    void setText(String text);

    String getdID();

    void setStyles (ITableStyle style);

    void parseJson(JSONObject json) throws JSONException;
}
