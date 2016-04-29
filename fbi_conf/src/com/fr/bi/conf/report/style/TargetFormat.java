package com.fr.bi.conf.report.style;

import com.fr.json.JSONObject;
import com.fr.json.JSONParser;


public class TargetFormat implements JSONParser {

    private int decimal_format = -1;

    private int symbol_format = -1;


    /**
     * format:{decimal_format:0, symbol_format:-1},
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("decimal_format")) {
            this.decimal_format = jo.getInt("decimal_format");
        }
        if (jo.has("symbol_format")) {
            this.symbol_format = jo.getInt("symbol_format");
        }
    }

    public String createFormatString() {
        return TargetStyleConstant.createFormatString(decimal_format, symbol_format);
    }
}