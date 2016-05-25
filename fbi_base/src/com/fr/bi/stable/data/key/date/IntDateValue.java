package com.fr.bi.stable.data.key.date;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public abstract class IntDateValue implements BIDateValue {
    @BICoreField
    protected int value;
    public IntDateValue(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}