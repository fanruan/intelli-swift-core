package com.fr.bi.stable.data.key.date;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.utils.time.BIDateUtils;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public class BIDayValue implements BIDateValue<Long>{
    @BICoreField
    private long value;

    public BIDayValue(long value) {
        this.value = BIDateUtils.toSimpleDay(value);
    }


    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}