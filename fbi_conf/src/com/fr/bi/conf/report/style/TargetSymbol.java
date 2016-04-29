package com.fr.bi.conf.report.style;

import com.fr.json.JSONObject;
import com.fr.json.JSONParser;


public class TargetSymbol implements JSONParser {

    private int symbol_type = 0;

    private double symbol_value;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("symbol_type")) {
            this.symbol_type = jo.getInt("symbol_type");
        }
        if (jo.has("symbol_value")) {
            this.setSymbol_value(jo.getDouble("symbol_value"));
        }
    }

    public boolean showAsHtml() {
        return symbol_type != 0;
    }

    public int getSymbol_type() {
        return symbol_type;
    }


    public double getSymbol_value() {
        return symbol_value;
    }

    private void setSymbol_value(double symbol_value) {
        this.symbol_value = symbol_value;
    }
}