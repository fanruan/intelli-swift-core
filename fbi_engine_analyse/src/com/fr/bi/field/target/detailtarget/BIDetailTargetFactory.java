package com.fr.bi.field.target.detailtarget;

import com.fr.bi.field.target.detailtarget.field.BIDateDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BINumberDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BIStringDetailTarget;
import com.fr.bi.field.target.detailtarget.formula.BINumberFormulaDetailTarget;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/16.
 */
public class BIDetailTargetFactory {

    public static BIDetailTarget parseTarget(JSONObject jo, long userId) throws Exception {
        BIDetailTarget target;
        int type = jo.optInt("type");
        switch (type) {
            case BIReportConstant.TARGET_TYPE.NUMBER: {
                target = new BINumberDetailTarget();
                break;
            }
            case BIReportConstant.TARGET_TYPE.DATE: {
                target = new BIDateDetailTarget();
                break;
            }
            case BIReportConstant.TARGET_TYPE.STRING: {
                target = new BIStringDetailTarget();
                break;
            }
            case BIReportConstant.TARGET_TYPE.FORMULA: {
                target = new BINumberFormulaDetailTarget();
                break;
            }
            default: {
                target = new BIStringDetailTarget();
            }
        }
        if (target != null) {
            target.parseJSON(jo, userId);
        }
        return target;
    }
}