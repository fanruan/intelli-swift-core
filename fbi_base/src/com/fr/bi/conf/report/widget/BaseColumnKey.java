package com.fr.bi.conf.report.widget;

import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;

/**
 * Created by GUY on 2015/4/20.
 */
public class BaseColumnKey extends BIField {
    public BaseColumnKey() {
    }

    public BaseColumnKey(BIField define) {
        super(define);
    }

    public BaseColumnKey(BITable define, String field) {
        super(define, field);
    }

    public BaseColumnKey(String id, String field) {
        super(id, field);
    }

    public BaseColumnKey createColumnKey() {
        return new BaseColumnKey(this);
    }
}