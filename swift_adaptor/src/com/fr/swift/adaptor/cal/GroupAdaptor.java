package com.fr.swift.adaptor.cal;

import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.auto.NumberAutoGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupNodeBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsItemBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomGroupBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomUnGroupValueBean;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionAutoGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionCustomGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.string.StringDimensionCustomGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionGroup;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.AutoGroupRule;
import com.fr.swift.query.group.impl.AutoGroupRule.Partition;
import com.fr.swift.query.group.impl.CustomGroupRule;
import com.fr.swift.query.group.impl.CustomGroupRule.StringGroup;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.query.group.impl.NoGroupRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/21
 */
public class GroupAdaptor {
    public static Group adaptGroup(FineDimensionGroup dimGroup) {
        GroupRule groupRule;
        GroupType type = GroupTypeAdaptor.adaptGroupType(dimGroup.getType());
        switch (type) {
            case AUTO:
                groupRule = newAutoRule(dimGroup);
                break;
            case CUSTOM_NUMBER:
                groupRule = newCustomNumberRule(dimGroup);
                break;
            case CUSTOM:
                groupRule = newCustomRule(dimGroup);
                break;
            default:
                groupRule = new NoGroupRule();
        }
        return Groups.newGroup(type, groupRule);
    }

    private static GroupRule newCustomRule(FineDimensionGroup dimGroup) {
        StringCustomGroupBean bean = ((StringDimensionCustomGroup) dimGroup).getValue();

        List<StringGroup> stringGroups = new ArrayList<StringGroup>();
        for (StringCustomDetailsBean detailsBean : bean.getDetails()) {
            String groupName = detailsBean.getValue();
            List<String> values = new ArrayList<String>();
            for (StringCustomDetailsItemBean itemBean : detailsBean.getContent()) {
                values.add(itemBean.getValue());
            }
            stringGroups.add(new StringGroup(groupName, values));
        }

        // 处理未分组的值
        // fixme 要通过ungroup2Other判断的 等andrew改
        boolean hasOtherGroup = bean.getUnGroup2OtherName() == null;

        if (hasOtherGroup) {
            // 有其他组则全部分到其他
            List<String> ungroupedValues = new ArrayList<String>();
            for (StringCustomUnGroupValueBean ungroupedBean : bean.getUnGroup()) {
                ungroupedValues.add(ungroupedBean.getValue());
            }
            stringGroups.add(new StringGroup(bean.getUnGroup2OtherName(), ungroupedValues));
        } else {
            // 无其他组则一个一组
            for (StringCustomUnGroupValueBean ungroupedBean : bean.getUnGroup()) {
                String value = ungroupedBean.getValue();
                stringGroups.add(new StringGroup(value, Collections.singletonList(value)));
            }
        }

        return new CustomGroupRule(stringGroups);
    }

    private static GroupRule newCustomNumberRule(FineDimensionGroup dimGroup) {
        NumberCustomGroupValueBean groupValue = ((NumberDimensionCustomGroup) dimGroup).getValue().getGroupValue();
        List<NumberCustomGroupNodeBean> beans = groupValue.getGroupNodes();

        List<NumInterval> intervals = new ArrayList<NumInterval>(beans.size());
        for (NumberCustomGroupNodeBean bean : beans) {
            intervals.add(new NumInterval(bean.getGroupName(),
                    bean.getMin(), bean.isCloseMin(),
                    bean.getMax(), bean.isCloseMax()));
        }

        return new CustomNumGroupRule(intervals, groupValue.getUseOther());
    }

    private static GroupRule newAutoRule(FineDimensionGroup group) {
        NumberAutoGroupValueBean bean = ((NumberDimensionAutoGroup) group).getValue().getGroupValue();
        return new AutoGroupRule(new Partition(bean.getMin(), bean.getMax(), bean.getGroupInterval()));
    }
}
