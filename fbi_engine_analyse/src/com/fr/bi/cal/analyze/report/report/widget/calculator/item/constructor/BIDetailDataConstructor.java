package com.fr.bi.cal.analyze.report.report.widget.calculator.item.constructor;

import com.fr.bi.cal.analyze.report.report.widget.calculator.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.calculator.item.ITableItem;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 * todo 少用json
 */
public class BIDetailDataConstructor extends BISummaryDataConstructor {

    public BIDetailDataConstructor(List<ITableHeader> headers, List<ITableItem> items, BIWidgetStyle widgetStyle) {
        super(headers, items, widgetStyle);
        super.widgetType=WidgetType.DETAIL.getType();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        JSONArray itemsArray = new JSONArray();
        if (null != items) {
            for (ITableItem item : items) {
                if (null != item) {
                    JSONArray itemArray = new JSONArray();
                    for (ITableItem it : item.getChildren()) {
                        itemArray.put(it.createJSON());
                    }
                    itemsArray.put(itemArray);
                }
            }
            jo.put("items", itemsArray);
        }
        jo.put("widgetType", widgetType);
        return jo;
    }

}
