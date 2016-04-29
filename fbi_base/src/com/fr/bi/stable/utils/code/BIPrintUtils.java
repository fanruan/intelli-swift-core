package com.fr.bi.stable.utils.code;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.general.DateUtils;

/**
 * System.println
 * Created by GUY on 2015/3/11.
 */
public class BIPrintUtils {
    /**
     * d写日志
     *
     * @param title title值
     * @param index index值
     * @param start start值
     */
    public static void writeIndexLog(String title, long index, long start) {
        BILogger.getLogger().info(title + ":" + index + " Cost:" + DateUtils.timeCostFrom(start));
    }

    public static void writeIndexLog(String title, long index, long rowCount, long start) {
        long time = System.currentTimeMillis() - start;
        time = (long) (((float) time / index) * rowCount) - time;
        BILogger.getLogger().info(title + ":" + (float) Math.round((float) index / rowCount * CubeConstant.PERCENT_ROW) / CubeConstant.PERCENT_ROW_D + "%! about " + DateUtils.miliisecondCostAsString(time) + "time left");
    }
}