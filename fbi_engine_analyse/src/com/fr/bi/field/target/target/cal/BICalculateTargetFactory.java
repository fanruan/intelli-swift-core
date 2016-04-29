package com.fr.bi.field.target.target.cal;

import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.cal.target.BIFormulaCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIPeriodConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BIRankConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BISummaryOfAboveConfiguredCalculateTarget;
import com.fr.bi.field.target.target.cal.target.configure.BISummaryOFAllConfiguredCalculateTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/8.
 */
public class BICalculateTargetFactory {

    public static BISummaryTarget parseTarget(JSONObject jo, long userId) throws Exception {
        //兼容之前的
        int cal_type = BIReportConstant.TARGET_TYPE.CAL.FORMULA;
        if (jo.has("type")) {
            cal_type = jo.getInt("type");
        }
        switch (cal_type) {
            case BIReportConstant.TARGET_TYPE.FORMULA: {
                BIFormulaCalculateTarget target = new BIFormulaCalculateTarget();
                target.parseJSON(jo, userId);
                return target;
            }
            case BIReportConstant.TARGET_TYPE.RANK: {
                return getRankConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.RANK_IN_GROUP: {
                return getRankConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.SUM_OF_ALL: {
                return getSummaryOfAllConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.SUM_OF_ALL_IN_GROUP: {
                return getSummaryOfAllConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE: {
                return getSummaryOfAboveConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP: {
                return getSummaryOfAboveConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_RATE: {
                return getPeriodConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_VALUE: {
                return getPeriodConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_RATE: {
                return getPeriodConfiguredTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_VALUE: {
                return getPeriodConfiguredTarget(jo, userId);
            }
        }
        return null;
    }

    private static BISummaryTarget getRankConfiguredTarget(JSONObject jo, long userId) throws Exception {
        BIRankConfiguredCalculateTarget target = new BIRankConfiguredCalculateTarget();
        target.parseJSON(jo, userId);
        return target;
    }

    private static BISummaryTarget getSummaryOfAllConfiguredTarget(JSONObject jo, long userId) throws Exception {
        BISummaryOFAllConfiguredCalculateTarget target = new BISummaryOFAllConfiguredCalculateTarget();
        target.parseJSON(jo, userId);
        return target;
    }

    private static BISummaryTarget getSummaryOfAboveConfiguredTarget(JSONObject jo, long userId) throws Exception {
        BISummaryOfAboveConfiguredCalculateTarget target = new BISummaryOfAboveConfiguredCalculateTarget();
        target.parseJSON(jo, userId);
        return target;
    }

    private static BISummaryTarget getPeriodConfiguredTarget(JSONObject jo, long userId) throws Exception {
        BIPeriodConfiguredCalculateTarget target = new BIPeriodConfiguredCalculateTarget();
        target.parseJSON(jo, userId);
        return target;
    }


}