package com.fr.bi.field.target.key.cal;

import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.general.ComparatorUtils;

import java.util.Map;

/**
 * doCalculator
 *
 * @author Daniel
 */
public abstract class BICalculatorTargetKey extends BIField implements BITargetKey {

    /**
     *
     */
    private static final long serialVersionUID = 1212276867649498993L;
    protected Map targetMap;
    private String targetName;

    public BICalculatorTargetKey(String targetName, Map targetMap) {
        super();
        this.targetName = targetName;
        this.targetMap = targetMap;
    }

    public String getTargetName() {
        return targetName;
    }

    /**
     * hashֵ
     *
     * @return hashֵ
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getSummaryType();
        result = prime * result
                + ((targetName == null) ? 0 : targetName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BICalculatorTargetKey other = (BICalculatorTargetKey) obj;
        if (targetName == null) {
            if (other.targetName != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(targetName, other.targetName)) {
            return false;
        }
        return true;
    }

}
