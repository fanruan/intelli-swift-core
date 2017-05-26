package com.fr.bi.stable.data.key.date;


import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.utils.time.BIDateUtils;

/**
 * Created by wang on 2017/5/12.
 */
public class BIYearMonthDayHourValue implements BIDateValue<Long>{
    private static final long serialVersionUID = 1740249305390162117L;
    @BICoreField
    private long value;

    public BIYearMonthDayHourValue(long value) {
        this.value = BIDateUtils.toYearMonthDayHour(value);
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

        BIYearMonthDayHourValue that = (BIYearMonthDayHourValue) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}

