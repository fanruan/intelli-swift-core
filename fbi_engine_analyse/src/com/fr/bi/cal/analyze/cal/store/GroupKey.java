package com.fr.bi.cal.analyze.cal.store;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.report.widget.field.dimension.filter.ResultFilter;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.algorithem.BIHashCodeUtils;
import com.fr.general.ComparatorUtils;

public class GroupKey {

    private DimensionCalculator[] key;
    private BusinessTable targetTableKey;
    private ResultFilter resultFilter;


    private boolean useRealData = true;

    public GroupKey(BusinessTable targetTableKey, DimensionCalculator[] key) {
        this(targetTableKey, key, null, true);
    }

    public GroupKey(BusinessTable targetTableKey, DimensionCalculator[] key, boolean useRealData) {
        this(targetTableKey, key, null, useRealData);
    }

    public GroupKey(BusinessTable targetTableKey, DimensionCalculator[] key, ResultFilter resultFilter) {
        this.targetTableKey = targetTableKey;
        this.key = key;
        this.resultFilter = resultFilter;
    }

    public GroupKey(BusinessTable targetTableKey, DimensionCalculator[] key, ResultFilter resultFilter, boolean useRealData) {
        this.targetTableKey = targetTableKey;
        this.key = key;
        this.resultFilter = resultFilter;
        this.useRealData = useRealData;
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
        result = prime * result + BIHashCodeUtils.hashCode(key);
        result = prime * result
                + ((targetTableKey == null) ? 0 : targetTableKey.hashCode());
        result = prime * result
                + ((resultFilter == null) ? 0 : resultFilter.hashCode());
        result = prime * result + (useRealData ? 1 : 0);
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
        GroupKey other = (GroupKey) obj;
        if (!equalsKey(other)) {
            return false;
        }
        if (targetTableKey == null) {
            if (other.targetTableKey != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(targetTableKey, other.targetTableKey)) {
            return false;
        }
        if (resultFilter == null) {
            if (other.resultFilter != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(resultFilter, other.resultFilter)) {
            return false;
        }
        if (!ComparatorUtils.equals(useRealData, other.useRealData)) {
            return false;
        }
        return true;
    }

    private boolean equalsKey(GroupKey other) {
        return ComparatorUtils.equals(key, other.key);
    }

}
