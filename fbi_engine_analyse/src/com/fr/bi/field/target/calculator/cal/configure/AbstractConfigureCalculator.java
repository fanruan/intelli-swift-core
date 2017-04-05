package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;

import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class AbstractConfigureCalculator extends CalCalculator {
    private static final long serialVersionUID = -7031889439937906167L;
    protected String target_id;

    /**
     * 哪个分组的排名， 默认是全部值
     */
    protected int start_group = BIReportConstant.TARGET_TYPE.CAL_POSITION.ALL;

    protected transient Object key;

    public AbstractConfigureCalculator(BIConfiguredCalculateTarget target, String target_id, int start_group) {
        super(target);
        this.target_id = target_id;
        this.start_group = start_group;
    }

    protected Object getCalKey() {
        if (key == null) {
            key = targetMap.get(target_id);
        }
        return key;
    }

    @Override
    public boolean isAllFieldsReady(Set<TargetGettingKey> targetSet) {
        Object key = getCalKey();
        if (key == null) {
            return false;
        }
        return targetSet.contains(getCalKey());
    }

    protected int getCalDeep(BINode rank_node) {
        int deep = 0;
        BINode node = rank_node;
        while (node.getFirstChild() != null) {
            deep++;
            node = node.getFirstChild();
        }

        return deep;
    }

    protected int getCalDeep(BICrossNode rank_node) {
        int deep = 0;
        BICrossNode node = rank_node;
        while (node.getLeftFirstChild() != null) {
            deep++;
            node = node.getLeftFirstChild();
        }
        return deep;
    }

    protected int getActualStart_Group(int start_group, BINode rank_node) {
        return start_group == 0 ? 0 : getCalDeep(rank_node) - 1;
    }

    protected int getActualStart_Group(int start_group, BICrossNode rank_node) {
        return start_group == 0 ? 0 : getCalDeep(rank_node);
    }

}