package com.fr.bi.conf.report.widget.field.filtervalue;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;

/**
 * Created by 小灰灰 on 2016/5/25.
 */
public abstract class AbstractFilterValue<T> implements FilterValue<T> {
    private static final long serialVersionUID = -2986872640968557442L;

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
