/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;


import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CrossNode4Calculate extends CrossNode {

    /**
     *
     */
    private static final long serialVersionUID = -386620484521317709L;


    protected volatile Map<TargetGettingKey, GroupValueIndex> index4Cal = new ConcurrentHashMap<TargetGettingKey, GroupValueIndex>(1);

    /**
     * @param head
     * @param left
     */
    public CrossNode4Calculate(CrossHeader head, CrossHeader left, TargetCalculator[] calculators) {
        super(head, left);
        for (int i = 0; i < calculators.length; i++) {
            GroupValueIndex headerGvi = this.getHead().getGroupValueIndex(calculators[i].createTargetGettingKey());
            GroupValueIndex h = headerGvi != null ? headerGvi : this.getHead().getTargetIndex(calculators[i].createTargetGettingKey());
            GroupValueIndex leftGvi = this.getLeft().getGroupValueIndex(calculators[i].createTargetGettingKey());
            GroupValueIndex l = leftGvi != null ? leftGvi : this.getLeft().getTargetIndex(calculators[i].createTargetGettingKey());
            GroupValueIndex gvi = getAndGvi(h, l);
            if (gvi != null) {
                this.index4Cal.put(calculators[i].createTargetGettingKey(), gvi);
            }
        }
    }

    private GroupValueIndex getAndGvi(GroupValueIndex gvi, GroupValueIndex gvi1) {

        if (gvi == null || gvi1 == null) {
            return null;
        }
        if (gvi == null) {
            return gvi1;
        }
        if (gvi1 == null) {
            return gvi;
        }
        return gvi.AND(gvi1);
    }


    @Override
    public GroupValueIndex getIndex4CalByTargetKey(TargetGettingKey key) {
        return this.index4Cal.get(key);
    }
}