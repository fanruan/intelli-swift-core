package com.fr.bi.field.target.target.cal.target.configure;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.field.target.calculator.cal.configure.RankConfigureCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.json.JSONObject;

public class BIRankConfiguredCalculateTarget extends BIConfiguredCalculateTarget {
    /**
     * 升序降序
     */
    @BICoreField
    private int rank_type;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("_src")) {
            JSONObject srcJo = jo.optJSONObject("_src");
            this.rank_type = srcJo.optJSONObject("expression").optInt("rankType");
        }
    }


    @Override
    public TargetCalculator createSummaryCalculator() {
        return new RankConfigureCalculator(this, getCalTargetKey(), getStart_group(), rank_type);
    }
}