package com.fr.bi.field.target.target.cal.target.configure;


import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.field.target.calculator.cal.configure.PeriodConfigureCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.json.JSONObject;

public class BIPeriodConfiguredCalculateTarget extends
        BIConfiguredCalculateTarget {
    @BICoreField
    private int periodType = BIReportConstant.TARGET_TYPE.CAL_VALUE.PERIOD_TYPE.VALUE;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("_src")) {
            JSONObject srcJo = jo.optJSONObject("_src");
            this.periodType = srcJo.optJSONObject("expression").optInt("periodType");
        }
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new PeriodConfigureCalculator(this, getCalTargetKey(), getStart_group(), periodType);
    }
}