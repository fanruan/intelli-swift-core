package com.fr.bi.field.target.key.cal.configuration.summary;


import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

/**
 * TODO 可以考虑使用Sum的value和count的value做除法
 *
 * @author Daniel
 */
public class BIAvgOfAllKey extends BISummaryOfAllCalTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = -1147344812297195470L;

    public BIAvgOfAllKey(String targetName, String cal_target_name,
                         Map targetMap, int start_group) {
        super(targetName, cal_target_name, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.AVG;
    }


}