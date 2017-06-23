package com.fr.bi.stable.operation.sort;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.NameObject;

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

    /**
     * 是否设置了指标排序
     * @param targetSort
     * @return
     */
    public static boolean hasTargetSort(NameObject targetSort){
        return  targetSort != null && (Integer)(targetSort.getObject()) != BIReportConstant.SORT.NONE;
    }
}
