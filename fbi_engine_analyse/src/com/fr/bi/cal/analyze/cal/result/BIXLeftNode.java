package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;

/**
 * Created by 小灰灰 on 2017/7/13.
 */
public interface BIXLeftNode extends BINode {
    Number[] getSubValues(XTargetGettingKey key);

    void setXValue(XTargetGettingKey key, Number sumValue);

    Number[][] getXValue();

    void setXValue(Number[][] xValue);
}
