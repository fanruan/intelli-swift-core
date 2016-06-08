package com.fr.bi.stable.operation.sort;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.sort.*;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/2/12.
 */
public class BISortFactory {

    public static ISort parseSort(JSONObject jo) throws Exception {
        ISort sort = null;
        int dimensionType = jo.optInt("dimension_type");
        int sortType = jo.optInt("type", BIReportConstant.SORT.ASC);
        if (dimensionType == BIReportConstant.TARGET_TYPE.NUMBER || dimensionType == BIReportConstant.TARGET_TYPE.DATE) {
            if (sortType == BIReportConstant.SORT.ASC) {
                sortType = BIReportConstant.SORT.NUMBER_ASC;
            }
            if (sortType == BIReportConstant.SORT.DESC) {
                sortType = BIReportConstant.SORT.NUMBER_DESC;
            }
        }
        switch (sortType) {
            case BIReportConstant.SORT.ASC:
                sort = new ASCSort();
                break;
            case BIReportConstant.SORT.DESC:
                sort = new DSCSort();
                break;
            case BIReportConstant.SORT.CUSTOM:
                sort = new CustomSort();
                break;
            case BIReportConstant.SORT.NONE:
                sort = new NoSort();
                break;
            case BIReportConstant.SORT.NUMBER_ASC:
                sort = new NumberASCSort();
                break;
            case BIReportConstant.SORT.NUMBER_DESC:
                sort = new NumberDSCSort();
                break;
            default:
                sort = new NoSort();
                break;
        }
        sort.parseJSON(jo);
        return sort;
    }
}