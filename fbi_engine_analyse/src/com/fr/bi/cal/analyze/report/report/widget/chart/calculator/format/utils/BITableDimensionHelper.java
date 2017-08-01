package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/26.
 * 指标维度属性
 */
public class BITableDimensionHelper {
    // FIXME: 2017/2/26 需要抽象出来
    public static int getFieldTypeByDimensionID(Map<Integer, List<JSONObject>> dimAndTar, String dId) throws Exception {
        JSONObject dimAndTarsJson = getDimAndTars(dimAndTar, dId);
        return dimAndTarsJson.getInt("type");
    }

    public static String getDimensionNameByID(Map<Integer, List<JSONObject>> dimAndTar, String dId) throws Exception {
        JSONObject dimAndTarsJson = getDimAndTars(dimAndTar, dId);
        return dimAndTarsJson.optString("text");
    }

    private static JSONObject getDimAndTars(Map<Integer, List<JSONObject>> dimAndTar, String dId) throws Exception {
        Iterator<Integer> iterator = dimAndTar.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            for (JSONObject jsonObject : dimAndTar.get(next)) {
                if (ComparatorUtils.equals(jsonObject.getString("dId"), dId)) {
                    return jsonObject;
                }
            }
        }
        throw new Exception();
    }

    public static boolean isDimUsed(Map<Integer, List<JSONObject>> dimAndTar, String dId) throws Exception {
        Iterator<Integer> iterator = dimAndTar.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            for (JSONObject jsonObject : dimAndTar.get(next)) {
                if (ComparatorUtils.equals(jsonObject.getString("dId"), dId)) {
                    if (jsonObject.has("used")) {
                        return jsonObject.getBoolean("used");
                    }
                }
            }
        }
        return true;
    }

    public static boolean isDimensionRegion1ByRegionType(int regionType) {
        return regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) && regionType < Integer.parseInt(BIReportConstant.REGION.DIMENSION2);
    }

    public static boolean isDimensionRegion2ByRegionType(int regionType) {
        return regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION2) && regionType < Integer.parseInt(BIReportConstant.REGION.TARGET1);
    }
}
