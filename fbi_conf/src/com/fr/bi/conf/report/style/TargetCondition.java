package com.fr.bi.conf.report.style;

import com.fr.base.Style;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.awt.*;


public class TargetCondition implements JSONParser {

    private double smallSide = Double.NEGATIVE_INFINITY;

    private double bigSide = Double.POSITIVE_INFINITY;

    private boolean isCloseOnSmallSide;

    private boolean isCloseOnBigSide;

    private Color rgbColor;


    /**
     * [{min:0 , max: 100, closemin: true, closemax: true, color: "rgb(255,0,0)"
     * }...]
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("min")) {
            this.smallSide = jo.getDouble("min");
        }
        if (jo.has("max")) {
            this.bigSide = jo.getDouble("max");
        }
        if (jo.has("closemin")) {
            this.isCloseOnSmallSide = jo.getBoolean("closemin");
        }
        if (jo.has("closemax")) {
            this.isCloseOnBigSide = jo.getBoolean("closemax");
        }
        if (jo.has("color")) {
            String c = jo.getString("color");
            String[] ss = c.split("\\D+");
            this.rgbColor = new Color(Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]));
        }
    }


    public Style deriveStyle(Style style, double value) {
        if (inRange(value)) {
            style = style.deriveFRFont(style.getFRFont().applyForeground(rgbColor));
        }
        return style;
    }

    private boolean inRange(double value) {
        return (isCloseOnSmallSide ? value >= smallSide : value > smallSide)
                && (isCloseOnBigSide ? value <= bigSide : value < bigSide);
    }


}