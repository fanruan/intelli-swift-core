package com.fr.bi.cal.analyze.data;

import com.fr.bi.stable.report.SummaryNode;
import com.fr.bi.stable.structure.Root;


/**
 * Created by GUY on 2015/4/30.
 */
public class SummaryRoot extends Root<BIValueKey> implements SummaryNode<BIValueKey> {

    public SummaryRoot(BIValueKey value) {
        super(value);
    }

    public SummaryRoot(BIValueKey value, Object data) {
        super(value, data);
    }

    @Override
    public void setSummaryValue(BIValueKey key, Object value) {

    }

    @Override
    public Number getSummaryValue(BIValueKey key) {
        return null;
    }

    @Override
    public void clear() {
        super.clear();
    }
}