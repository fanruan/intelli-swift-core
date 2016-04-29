package com.fr.bi.field.target.target.cal.target.configure;

import com.fr.bi.field.target.calculator.cal.configure.RankConfigureCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.json.JSONObject;

public class BIRankConfiguredCalculateTarget extends BIConfiguredCalculateTarget {
    /**
     * 升序降序
     */
    private int rank_type;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("_src")) {
            JSONObject srcJo = jo.optJSONObject("_src");
            this.rank_type = srcJo.optJSONObject("expression").optInt("rank_type");
        }
    }


    @Override
    public TargetCalculator createSummaryCalculator() {
        return new RankConfigureCalculator(this, getCalTargetName(), getStart_group(), rank_type);
    }
}