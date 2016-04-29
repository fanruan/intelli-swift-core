package com.fr.bi.field.target.key.cal.configuration;


import com.fr.bi.field.target.key.cal.BICalculatorTargetKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;

import java.util.Map;

public abstract class BIConfiguratedCalculatorTargetKey extends BICalculatorTargetKey {
    /**
     *
     */
    private static final long serialVersionUID = 484959959179177185L;
    /**
     * 哪个分组的排名， 默认是全部值
     */
    protected int start_group = BIReportConstant.TARGET_TYPE.CAL_POSITION.ALL;
    private String cal_target_name;

    public BIConfiguratedCalculatorTargetKey(String targetName, String cal_target_name, Map targetMap, int start_group) {
        super(targetName, targetMap);
        this.cal_target_name = cal_target_name;
        this.start_group = start_group;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cal_target_name == null) ? 0 : cal_target_name.hashCode());
        result = prime * result + start_group;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BIConfiguratedCalculatorTargetKey other = (BIConfiguratedCalculatorTargetKey) obj;
        if (cal_target_name == null) {
            if (other.cal_target_name != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(cal_target_name, other.cal_target_name)) {
            return false;
        }
        if (start_group != other.start_group) {
            return false;
        }
        return true;
    }


}