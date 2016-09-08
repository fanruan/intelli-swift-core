package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public abstract class BIAbstractChartSetting implements BIChartSetting {

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types) throws JSONException{
        JSONArray result = new JSONArray();
        int yAxisIndex = 0;
        for(int i = 0; i < data.length(); i++){
            JSONArray belongAxisItems = data.getJSONArray(i);
            JSONArray combineItems = combineChildItems(types.getJSONArray(i), belongAxisItems);
            for(int j = 0; j < combineItems.length(); j++){
                JSONObject axisItems = combineItems.getJSONObject(j);
                result.put(axisItems.put("yAxis", yAxisIndex));
            }
            if(combineItems.length() > 0){
                yAxisIndex ++;
            }
        }
        JSONObject config = combineConfig();
        return new JSONObject().put("result", result).put("config", config);
    }

    private JSONArray combineChildItems(JSONArray items, JSONArray types) throws JSONException{
        JSONArray result = new JSONArray();
        for(int i = 0; i < items.length(); i++){
            JSONArray res = new JSONArray();
            JSONObject item = items.getJSONObject(i);
            res.put(formatChildItem(types.getInt(i), item));
        }
        return result;
    }

    private JSONObject formatChildItem(int type, JSONObject items) throws JSONException{
        switch (type) {
            case BIReportConstant.WIDGET.BAR:
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
            case BIReportConstant.WIDGET.COMPARE_BAR:
                items.put("type", "bar");
                break;
            case BIReportConstant.WIDGET.BUBBLE:
            case BIReportConstant.WIDGET.FORCE_BUBBLE:
                items.put("type",  "bubble");
                break;
            case BIReportConstant.WIDGET.SCATTER:
                items.put("type", "scatter");
                break;
            case BIReportConstant.WIDGET.AXIS:
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.COMPARE_AXIS:
            case BIReportConstant.WIDGET.FALL_AXIS:
                items.put("type", "column");
                break;
            case BIReportConstant.WIDGET.LINE:
                items.put("type", "line");
                break;
            case BIReportConstant.WIDGET.AREA:
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
            case BIReportConstant.WIDGET.COMPARE_AREA:
            case BIReportConstant.WIDGET.RANGE_AREA:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA:
                items.put("type", "area");
                break;
            case BIReportConstant.WIDGET.DONUT:
                items.put("type", "pie");
                break;
            case BIReportConstant.WIDGET.RADAR:
            case BIReportConstant.WIDGET.ACCUMULATE_RADAR:
                items.put("type", "radar");
                break;
            case BIReportConstant.WIDGET.PIE:
                items.put("type", "pie");
                break;
            case BIReportConstant.WIDGET.DASHBOARD:
                items.put("type", "gauge");
                break;
            case BIReportConstant.WIDGET.MAP:
                items.put("type", "areaMap");
                break;
            case BIReportConstant.WIDGET.GIS_MAP:
                items.put("type", "pointMap");
                break;
            default:
                items.put("type", "column");
                break;
        }
        return items;
    }

    private JSONObject combineConfig() throws JSONException{
        return new JSONObject("{\"plotOptions\":{\"rotatable\":false,\"startAngle\":0,\"borderRadius\":0,\"endAngle\":360,\"innerRadius\":\"0.0%\",\"layout\":\"horizontal\",\"hinge\":\"rgb(101,107,109)\",\"dataLabels\":{\"style\":{\"fontFamily\":\"inherit\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"formatter\":{\"identifier\":\"${VALUE}\",\"valueFormat\": "+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT + ",\"XFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"YFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"sizeFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +"},\"align\":\"outside\",\"enabled\":false},\"percentageLabel\":{\"formatter\":{\"identifier\":\"${PERCENT}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"bottom\",\"enabled\":true},\"valueLabel\":{\"formatter\":{\"identifier\":\"${SERIES}${VALUE}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"backgroundColor\":\"rgb(255,255,0)\",\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"inside\",\"enabled\":true},\"hingeBackgroundColor\":\"rgb(220,242,249)\",\"seriesLabel\":{\"formatter\":{\"identifier\":\"${CATEGORY}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"bottom\",\"enabled\":true},\"style\":\"pointer\",\"paneBackgroundColor\":\"rgb(252,252,252)\",\"needle\":\"rgb(229,113,90)\",\"large\":false,\"connectNulls\":false,\"shadow\":true,\"curve\":false,\"sizeBy\":\"area\",\"tooltip\":{\"formatter\":{\"identifier\":\"${SERIES}${X}${Y}${SIZE}{CATEGORY}${SERIES}${VALUE}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"XFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"sizeFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"YFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +"},\"shared\":false,\"padding\":5,\"backgroundColor\":\"rgba(0,0,0,0.4980392156862745)\",\"borderColor\":\"rgb(0,0,0)\",\"shadow\":false,\"borderRadius\":2,\"borderWidth\":0,\"follow\":false,\"enabled\":true,\"animation\":true,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#c4c6c6\",\"fontSize\":\"12px\",\"fontWeight\":\"\"}},\"maxSize\":80,\"fillColorOpacity\":1,\"step\":false,\"force\":false,\"minSize\":15,\"displayNegative\":true,\"categoryGap\":\"16.0%\",\"borderColor\":\"rgb(255,255,255)\",\"borderWidth\":1,\"gap\":\"22.0%\",\"animation\":true,\"lineWidth\":2,\"bubble\":{\"large\":false,\"connectNulls\":false,\"shadow\":true,\"curve\":false,\"sizeBy\":\"area\",\"maxSize\":80,\"minSize\":15,\"lineWidth\":0,\"animation\":true,\"fillColorOpacity\":0.699999988079071,\"marker\":{\"symbol\":\"circle\",\"radius\":28.39695010101295,\"enabled\":true}}},\"dTools\":{\"enabled\":false,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#1a1a1a\",\"fontSize\":\"12px\"},\"backgroundColor\":\"white\"},\"dataSheet\":{\"enabled\":false,\"borderColor\":\"rgb(0,0,0)\",\"borderWidth\":1,\"formatter\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"}},\"borderColor\":\"rgb(238,238,238)\",\"shadow\":false,\"legend\":{\"borderColor\":\"rgb(204,204,204)\",\"borderRadius\":0,\"shadow\":false,\"borderWidth\":0,\"visible\":true,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#1a1a1a\",\"fontSize\":\"12px\"},\"position\":\"right\",\"enabled\":false},\"rangeLegend\":{\"range\":{\"min\":0,\"color\":[[0,\"rgb(182,226,255)\"],[0.5,\"rgb(109,196,255)\"],[1,\"rgb(36,167,255)\"]],\"max\":266393},\"enabled\":false},\"zoom\":{\"zoomType\":\"xy\",\"zoomTool\":{\"visible\":false,\"resize\":true,\"from\":\"\",\"to\":\"\"}},\"plotBorderColor\":\"rgba(255,255,255,0)\",\"tools\":{\"hidden\":true,\"toImage\":{\"enabled\":true},\"sort\":{\"enabled\":true},\"enabled\":false,\"fullScreen\":{\"enabled\":true}},\"plotBorderWidth\":0,\"colors\":[\"rgb(99,178,238)\",\"rgb(118,218,145)\"],\"borderRadius\":0,\"borderWidth\":0,\"style\":\"normal\",\"plotShadow\":false,\"plotBorderRadius\":0}");
    }

    public JSONObject getFontStyle() throws JSONException{
        return new JSONObject("{" +
                "fontFamily:" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "," +
                "color:" + BIChartSettingConstant.FONT_STYLE.COLOR + "," +
                "fontSize: " + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "}"
        );
    }
}
