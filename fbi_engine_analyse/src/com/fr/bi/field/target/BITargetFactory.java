package com.fr.bi.field.target;

import com.fr.bi.field.target.target.BICounterTarget;
import com.fr.bi.field.target.target.BINumberTarget;
import com.fr.bi.field.target.target.BIStringTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.cal.BICalculateTargetFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/7.
 */
public class BITargetFactory {

    /**
     * 根据json创建target
     *
     * @param jo     json对象
     * @param userId 用户id
     * @return 创建的target
     * @throws Exception
     */
    public static BISummaryTarget parseTarget(JSONObject jo, long userId) throws Exception {
        int target_type = jo.getInt("type");
        switch (target_type) {
            case BIReportConstant.TARGET_TYPE.NUMBER: {
                BINumberTarget target = new BINumberTarget();
                target.parseJSON(jo, userId);
                return target;
            }
            case BIReportConstant.TARGET_TYPE.RANK:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.RANK_IN_GROUP:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.SUM_OF_ALL:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_RATE:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_RATE:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
                return BICalculateTargetFactory.parseTarget(jo, userId);
            case BIReportConstant.TARGET_TYPE.FORMULA: {
                return BICalculateTargetFactory.parseTarget(jo, userId);
            }
            case BIReportConstant.TARGET_TYPE.COUNTER: {
                BICounterTarget target = new BICounterTarget();
                target.parseJSON(jo, userId);
                return target;
            }
            case BIReportConstant.TARGET_TYPE.STRING: {
                BIStringTarget target = new BIStringTarget();
                target.parseJSON(jo, userId);
                return target;
            }


        }
        return null;
    }
}