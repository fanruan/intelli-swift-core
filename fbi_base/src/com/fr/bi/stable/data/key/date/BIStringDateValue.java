package com.fr.bi.stable.data.key.date;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;

/**
 * Created by andrew_asa on 2017/5/16.
 */
public class BIStringDateValue implements BIDateValue<String>{

    String value;

    BIStringDateValue(String value){
        this.value = value;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }

    @Override
    public String getValue() {
        return value;
    }
}
