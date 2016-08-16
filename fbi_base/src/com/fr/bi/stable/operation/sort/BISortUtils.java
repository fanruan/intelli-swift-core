package com.fr.bi.stable.operation.sort;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by Root on 2016/8/10.
 */
public class BISortUtils {
    public static int getSortTypeByDimensionType(int sortType, int dimensionType) {
        if (dimensionType != BIReportConstant.TARGET_TYPE.STRING) {
            if (sortType == BIReportConstant.SORT.ASC) {
                sortType = BIReportConstant.SORT.NUMBER_ASC;
            }
            if (sortType == BIReportConstant.SORT.DESC) {
                sortType = BIReportConstant.SORT.NUMBER_DESC;
            }
        }
        return sortType;
    }
}
