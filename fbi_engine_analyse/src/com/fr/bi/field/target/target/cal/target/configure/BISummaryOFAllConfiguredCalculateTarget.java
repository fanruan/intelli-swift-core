package com.fr.bi.field.target.target.cal.target.configure;


import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.calculator.cal.configure.AvgOfAllCalculator;
import com.fr.bi.field.target.calculator.cal.configure.MaxOfAllCalculator;
import com.fr.bi.field.target.calculator.cal.configure.MinOfAllCalculator;
import com.fr.bi.field.target.calculator.cal.configure.SumOfAllCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *         所有值 的计算
 */
public class BISummaryOFAllConfiguredCalculateTarget extends
        BIConfiguredCalculateTarget {

    /**
     *
     */
    private static final long serialVersionUID = -2424220440722738333L;
    @BICoreField
    private int summary_type;

    @Override
    public TargetCalculator createSummaryCalculator() {
        switch (summary_type) {
            case BIReportConstant.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM: {
                return new SumOfAllCalculator(this, targetMap.get(getCalTargetName()), getStart_group());
            }
            case BIReportConstant.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX: {
                return new MaxOfAllCalculator(this, getCalTargetKey(), getStart_group());
            }
            case BIReportConstant.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN: {
                return new MinOfAllCalculator(this, getCalTargetKey(), getStart_group());
            }
            case BIReportConstant.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG: {
                return new AvgOfAllCalculator(this, getCalTargetKey(), getStart_group());
            }
            default: {
                return new SumOfAllCalculator(this, targetMap.get(getCalTargetName()), getStart_group());
            }
        }
    }

    public boolean calculateSingleNode(BITarget[] usedTargets){
        return calculateAllNode() || (summary_type !=  BIReportConstant.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM && isSumCalTarget(usedTargets));
    }


    public boolean isSumCalTarget(BITarget[] usedTargets) {
        BITarget target = BITravalUtils.getTargetByName(getCalTargetName(), usedTargets);
        return target != null && (target.getSummaryType() == BIReportConstant.SUMMARY_TYPE.COUNT || target.getSummaryType() == BIReportConstant.SUMMARY_TYPE.SUM);
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("_src")) {
            JSONObject srcJo = jo.optJSONObject("_src");
            this.summary_type = srcJo.optJSONObject("expression").optInt("summaryType");
        }
    }


}