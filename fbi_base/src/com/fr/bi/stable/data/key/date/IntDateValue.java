package com.fr.bi.stable.data.key.date;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public abstract class IntDateValue implements BIDateValue {
    protected int value;
    public IntDateValue(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}