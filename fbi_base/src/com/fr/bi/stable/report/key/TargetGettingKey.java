package com.fr.bi.stable.report.key;

import com.fr.bi.stable.report.result.BITargetKey;

public class TargetGettingKey {

    private BITargetKey targetKey;
    private String targetName;


    public TargetGettingKey(BITargetKey targetKey, String targetName) {
        this.targetKey = targetKey;
        this.targetName = targetName;
    }

    public BITargetKey getTargetKey() {
        return targetKey;
    }

    public String getTargetName() {
        return targetName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((targetKey == null) ? 0 : targetKey.hashCode());
        result = prime * result
                + ((targetName == null) ? 0 : targetName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TargetGettingKey other = (TargetGettingKey) obj;
        if (targetKey == null) {
            if (other.targetKey != null)
                return false;
        } else if (!targetKey.equals(other.targetKey))
            return false;
        if (targetName == null) {
            if (other.targetName != null)
                return false;
        } else if (!targetName.equals(other.targetName))
            return false;
        return true;
    }

}