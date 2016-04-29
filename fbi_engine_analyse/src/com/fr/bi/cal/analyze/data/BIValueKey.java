package com.fr.bi.cal.analyze.data;

import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

/**
 * 某个维度或指标值的key
 * Created by GUY on 2015/4/27.
 */
public class BIValueKey extends BIField {

    private String name;
    private Object value;

    private GroupValueIndex gvi;

    private BITargetAndDimension dimension;

    public BIValueKey(BITargetAndDimension dimension, Object value, GroupValueIndex gvi) {
        super(dimension.createColumnKey());
        this.dimension = dimension;
        this.name = dimension.getValue();
        this.value = value;
        this.gvi = gvi;
    }

    public BITargetAndDimension getDimension() {
        return dimension;
    }

    public Object getValue() {
        return value;
    }

    public GroupValueIndex getGroupValueIndex() {
        return gvi;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = result + 31 * (value == null ? 0 : value.hashCode());
        result = result + 31 * (name == null ? 0 : name.hashCode());
        result = result + 31 * (gvi == null ? 0 : gvi.hashCode());
        return result;
    }

    /**
     * equals方法
     */
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
        BIValueKey key = (BIValueKey) obj;
        if (!ComparatorUtils.equals(value, key.value)) {
            return false;
        }
        if (!ComparatorUtils.equals(name, key.name)) {
            return false;
        }
        if (!ComparatorUtils.equals(gvi, key.gvi)) {
            return false;
        }
        return super.equals(obj);
    }
}