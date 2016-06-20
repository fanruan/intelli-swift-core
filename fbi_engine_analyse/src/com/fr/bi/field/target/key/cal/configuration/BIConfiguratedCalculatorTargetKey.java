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
    private String target_id;

    public BIConfiguratedCalculatorTargetKey(String targetName, String target_id, Map targetMap, int start_group) {
        super(targetName, targetMap);
        this.target_id = target_id;
        this.start_group = start_group;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((target_id == null) ? 0 : target_id.hashCode());
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
        if (target_id == null) {
            if (other.target_id != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(target_id, other.target_id)) {
            return false;
        }
        if (start_group != other.start_group) {
            return false;
        }
        return true;
    }


}