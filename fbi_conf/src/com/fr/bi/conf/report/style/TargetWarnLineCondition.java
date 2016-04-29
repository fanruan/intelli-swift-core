package com.fr.bi.conf.report.style;

import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.awt.*;

/**
 * Created by sheldon on 14-5-27.
 */
public class TargetWarnLineCondition implements JSONParser {
    private Color color;

    private String name;

    private int type;

    private double defineNumber;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {

        if (jo.has("color")) {
            String c = jo.getString("color");
            String[] ss = c.split("\\D+");
            this.color = new Color(Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]));
        }

        if (jo.has("name")) {
            this.name = jo.getString("name");
        }

        if (jo.has("type")) {
            this.type = jo.getInt("type");

            //平均值是0， 自定义是1
            if (this.type == 1 && jo.has("defineValue")) {

                this.defineNumber = jo.getDouble("defineValue");
            } else {


            }
        }

    }

    public void setAverageValue(int value) {

        if (this.type == 0) {
            this.defineNumber = value;
        }
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public double getDefineNumber() {
        return defineNumber;
    }

    public int getType() {
        return type;
    }
}