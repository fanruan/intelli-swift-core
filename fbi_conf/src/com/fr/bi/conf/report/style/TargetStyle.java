package com.fr.bi.conf.report.style;

import com.fr.base.Style;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.report.cell.AbstractAnalyCellElementFull;
import com.fr.report.cell.cellattr.CellGUIAttr;

public class TargetStyle implements JSONParser {

    private TargetFormat format = new TargetFormat();
    private TargetSymbol symbol = new TargetSymbol();
    private TargetCondition[] condition = new TargetCondition[0];
    private TargetWarnLineCondition[] warnConditions = new TargetWarnLineCondition[0];

    public TargetWarnLineCondition[] getWarnConditions() {
        return warnConditions;
    }

    /**
     * color_condition:[],
     * format:{decimal_format:0, symbol_format:-1},
     * symbol_condition:{symbol_type:1, symbol_value:60}
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("format")) {
            format = new TargetFormat();
            format.parseJSON(jo.getJSONObject("format"));
        }
        if (jo.has("symbol_condition")) {
            symbol = new TargetSymbol();
            symbol.parseJSON(jo.getJSONObject("symbol_condition"));
        }
        if (jo.has("color_condition")) {
            JSONArray ja = jo.getJSONArray("color_condition");
            condition = new TargetCondition[ja.length()];
            for (int i = 0, len = ja.length(); i < len; i++) {
                condition[i] = new TargetCondition();
                condition[i].parseJSON(ja.getJSONObject(i));
            }
        }
        if (jo.has("scale_condition")) {
            JSONArray ja = jo.getJSONArray("scale_condition");
            warnConditions = new TargetWarnLineCondition[ja.length()];

            for (int i = 0, len = ja.length(); i < len; i++) {
                warnConditions[i] = new TargetWarnLineCondition();
                warnConditions[i].parseJSON(ja.getJSONObject(i));
            }
        }
    }

    //给指标加颜色
    public void changeCellStyle(AbstractAnalyCellElementFull cell) {
        Object v = cell.getValue();
        if (v instanceof Number) {
            Style style = cell.getStyle();
            CellGUIAttr attr = cell.getCellGUIAttr();
            if (attr == null) {
                attr = new CellGUIAttr();
            }
            boolean showAsHtml = symbol.showAsHtml();
            if (showAsHtml) {
                attr.setShowAsHTML(showAsHtml);
                String value = TargetStyleConstant.creatHtmlCellContent(format.createFormatString(),
                        ((Number) v).doubleValue(), symbol);
                cell.setValue(value);
                cell.setCellGUIAttr(attr);
            } else {
                style = style.deriveFormat(TargetStyleConstant.getFormat(format.createFormatString()));
            }
            for (int i = 0, len = condition.length; i < len; i++) {
                style = condition[i].deriveStyle(style, new Double(((Number) v).doubleValue()));
            }
            cell.setStyle(style);
        }
    }

    //给图表加警戒线
    public void addWarnLines() {

    }

}