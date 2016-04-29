package com.fr.bi.cal.analyze.cal.store;

import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.general.ComparatorUtils;

import java.util.Arrays;

/**
 * Created by 小灰灰 on 2014/4/2.
 */
public class CrossCalculatorKey {
    private DimensionCalculator[] row;
    private DimensionCalculator[] col;
    private Table targetTableKey;

    public CrossCalculatorKey(Table targetTableKey, DimensionCalculator[] row, DimensionCalculator[] col) {
        this.targetTableKey = targetTableKey;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CrossCalculatorKey that = (CrossCalculatorKey) o;

        if (!ComparatorUtils.equals(col, that.col)) {
            return false;
        }
        if (!ComparatorUtils.equals(row, that.row)) {
            return false;
        }
        if (targetTableKey != null ? !ComparatorUtils.equals(targetTableKey, that.targetTableKey) : that.targetTableKey != null) {
            return false;
        }

        return true;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        int result = row != null ? Arrays.hashCode(row) : 0;
        result = 31 * result + (col != null ? Arrays.hashCode(col) : 0);
        result = 31 * result + (targetTableKey != null ? targetTableKey.hashCode() : 0);
        return result;
    }
}