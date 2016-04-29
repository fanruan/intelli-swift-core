package com.fr.bi.field.target.key.sum;


import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;

/**
 * Created by 小灰灰 on 2014/12/18.
 */
public class NoneTargetCountKey extends CountKey {
    private static final long serialVersionUID = 1247023661192846526L;

    public NoneTargetCountKey(BIField ck, TargetFilter filter) {
        super(ck, filter);
    }

    public NoneTargetCountKey(BIField ck, TargetFilter filter, BIKey distinct_field) {
        super(ck, filter, distinct_field);
    }

}