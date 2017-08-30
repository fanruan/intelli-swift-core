package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils;

import com.fr.bi.conf.report.conf.dimension.BIDimensionConf;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/26.
 * 指标维度属性
 */
public class BITableDimensionHelper {
    // FIXME: 2017/2/26 需要抽象出来
    public static int getFieldTypeByDimensionID(Map<Integer, BIDimensionConf[]> dimAndTar, String dId) throws Exception {
        return getDimAndTars(dimAndTar, dId).getDimensionType();
    }

    public static String getDimensionNameByID(Map<Integer, BIDimensionConf[]> dimAndTar, String dId) throws Exception {
        return getDimAndTars(dimAndTar, dId).getDimensionName();
    }

    private static BIDimensionConf getDimAndTars(Map<Integer, BIDimensionConf[]> dimAndTar, String dId) throws Exception {
        Iterator<Integer> iterator = dimAndTar.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            for (BIDimensionConf dimConf : dimAndTar.get(next)) {
                if (ComparatorUtils.equals(dimConf.getDimensionID(), dId)) {
                    return dimConf;
                }
            }
        }
        throw new Exception();
    }

    public static boolean isDimUsed(Map<Integer, BIDimensionConf[]> dimAndTar, String dId) throws Exception {
        for (Integer next : dimAndTar.keySet()) {
            for (BIDimensionConf dimConf : dimAndTar.get(next)) {
                if (ComparatorUtils.equals(dimConf.getDimensionID(), dId)) {
                    if (dimConf.isDimensionUsed()) {
                        return dimConf.isDimensionUsed();
                    }
                }
            }
        }
        return false;
    }

    public static boolean isDimensionRegion1ByRegionType(int regionType) {
        return regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) && regionType < Integer.parseInt(BIReportConstant.REGION.DIMENSION2);
    }

    public static boolean isDimensionRegion2ByRegionType(int regionType) {
        return regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION2) && regionType < Integer.parseInt(BIReportConstant.REGION.TARGET1);
    }
}
