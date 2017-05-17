package com.fr.bi.stable.data.key.date;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.utils.time.BIDateUtils;

/**
 * Created by wang on 2017/5/12.
 */
public class BIYearSeasonValue implements BIDateValue<Long>{
    private static final long serialVersionUID = 1740249305390162117L;
    @BICoreField
    private long value;

    public BIYearSeasonValue(long value) {
        this.value = BIDateUtils.toYearSeason(value);
    }


    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BIYearSeasonValue that = (BIYearSeasonValue) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}