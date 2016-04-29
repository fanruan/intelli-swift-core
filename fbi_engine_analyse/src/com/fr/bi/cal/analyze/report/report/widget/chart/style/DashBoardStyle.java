package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.base.Formula;
import com.fr.chart.chartattr.MeterPlot;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.MapHotAreaColor;
import com.fr.chart.chartglyph.MeterInterval;
import com.fr.chart.chartglyph.MeterStyle;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.awt.*;

public class DashBoardStyle implements ChartStyle {

    private String unit = "km/h";

    private int speed_range = 270;

    private ColorGroup[] colorGroup = new ColorGroup[0];

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("unit_type")) {
            int unit_type = jo.getInt("unit_type");

           /* FR.BIBaseConstants.NUMBER_UNIT_ITEMS = [{
                text: "$", value: FR.BIBaseConstants.NUMBER_UNIT_DOLLAR
            },  {
                text: "￥", value: FR.BIBaseConstants.NUMBER_UNIT_YUAN
            },  {
                text: "%", value: FR.BIBaseConstants.NUMBER_UNIT_PERCENT
            }];*/

            //如果是自定义的话
            if (unit_type == 3 && jo.has("unit")) {
                this.unit = jo.getString("unit");
            } else {
                switch (unit_type) {
                    case 0:
                        this.unit = "$";
                        break;
                    case 1:
                        this.unit = "￥";
                        break;
                    case 2:
                        this.unit = "%";
                        break;
                }
            }
        }

        if (jo.has("speed_range")) {
            this.speed_range = jo.getInt("speed_range");
        }
        /*if(jo.has("unit")){
			this.unit = jo.getString("unit");
		}*/

        if (jo.has("speed_groups")) {
            JSONArray ja = jo.getJSONArray("speed_groups");
            colorGroup = new ColorGroup[ja.length()];
            for (int i = 0, len = ja.length(); i < len; i++) {
                colorGroup[i] = new ColorGroup();
                colorGroup[i].parseJSON(ja.getJSONObject(i));
            }
        }
    }

    @Override
    public void dealWithStyle(Plot plot) {
        if (plot instanceof MeterPlot) {
            MeterStyle style = ((MeterPlot) plot).getMeterStyle();
            style.setDialShape(MeterStyle.PIE270);
            style.setMaxArrowAngle(270);
            style.setMeterAngle(270);
//            style.setDialShape( this.speed_range );
            style.setMaxArrowAngle(this.speed_range);
            style.setMeterAngle(this.speed_range);
            style.setUnits(unit);

            if (colorGroup.length > 0) {
                style.setDesignTyle(MeterStyle.CUSTOM);
                style.setMapHotAreaColor(new MapHotAreaColor());
                double start = colorGroup[0].start;
                double end = colorGroup[colorGroup.length - 1].end;
                style.setStartValue(new Formula("=" + start));
                style.setEndValue(new Formula("=" + end));
                style.setTickLabelsVisible(true);
                style.clearAllInterval();
                for (int i = 0; i < colorGroup.length; i++) {
                    ColorGroup cg = colorGroup[i];
                    MeterInterval interval = new MeterInterval(Inter.getLocText("Chart_Meter_Field") + i,
                            new Formula("=" + cg.start), new Formula("=" + cg.end));
					/*interval.setBackgroundColor(TargetStyleConstant.CONDITION_COLOR[cg.colorIndex]);*/

                    interval.setBackgroundColor(cg.color);
                    style.addInterval(interval);
                }
                style.convertList2AreaColor();
            } else {
                style.setDesignTyle(MeterStyle.AUTO);
                style.convertAreaColor2List(0);
            }
        }
    }

    private class ColorGroup implements JSONParser {
        /*	private int colorIndex = 0;*/
        private Color color;
        private double start;
        private double end;

        private ColorGroup() {
        }

        @Override
        public void parseJSON(JSONObject jo) throws Exception {
			/*if(jo.has("smallNode")){
				this.start = jo.getDouble("smallNode");
			}
			if(jo.has("bigNode")){
				this.end = jo.getDouble("bigNode");
			}*/
            if (jo.has("max")) {
                this.start = jo.getDouble("min");
            }

            if (jo.has("min")) {
                this.end = jo.getDouble("max");
            }

            if (jo.has("color")) {
				/*this.colorIndex = jo.getInt("color");*/
                String c = jo.getString("color");
                String[] ss = c.split("\\D+");
                this.color = new Color(Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]));
            }

        }

    }
}