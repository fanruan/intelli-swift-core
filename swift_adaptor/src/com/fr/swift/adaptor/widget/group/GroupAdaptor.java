package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.auto.NumberAutoGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupNodeBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsItemBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomGroupValueBean;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionAutoGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionCustomGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.string.StringDimensionCustomGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionGroup;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.AutoNumGroupRule;
import com.fr.swift.query.group.impl.AutoNumGroupRule.Partition;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.query.group.impl.CustomStrGroupRule;
import com.fr.swift.query.group.impl.CustomStrGroupRule.StringGroup;
import com.fr.swift.query.group.impl.NoGroupRule;

import java.util.ArrayList;
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
                groupRule = newAutoRule((NumberDimensionAutoGroup) dimGroup);
                break;
            case CUSTOM_NUMBER:
                groupRule = newCustomNumberRule((NumberDimensionCustomGroup) dimGroup);
                break;
            case CUSTOM:
                groupRule = newCustomRule((StringDimensionCustomGroup) dimGroup);
                break;
            default:
                groupRule = new NoGroupRule();
        }
        return Groups.newGroup(type, groupRule);
    }

    private static GroupRule newCustomRule(StringDimensionCustomGroup dimGroup) {
        StringCustomGroupValueBean bean = dimGroup.getValue().getValue();

        List<StringGroup> stringGroups = new ArrayList<StringGroup>();
        for (StringCustomDetailsBean detailsBean : bean.getDetails()) {
            String groupName = detailsBean.getValue();
            List<String> values = new ArrayList<String>();
            for (StringCustomDetailsItemBean itemBean : detailsBean.getContent()) {
                values.add(itemBean.getValue());
            }
            stringGroups.add(new StringGroup(groupName, values));
        }

        return new CustomStrGroupRule(stringGroups, bean.getUseOther());
    }

    private static GroupRule newCustomNumberRule(NumberDimensionCustomGroup dimGroup) {
        NumberCustomGroupValueBean groupValue = dimGroup.getValue().getGroupValue();
        List<NumberCustomGroupNodeBean> beans = groupValue.getGroupNodes();

        List<NumInterval> intervals = new ArrayList<NumInterval>(beans.size());
        for (NumberCustomGroupNodeBean bean : beans) {
            intervals.add(new NumInterval(bean.getGroupName(),
                    bean.getMin(), bean.isCloseMin(),
                    bean.getMax(), bean.isCloseMax()));
        }

        return new CustomNumGroupRule(intervals, groupValue.getUseOther());
    }

    private static GroupRule newAutoRule(NumberDimensionAutoGroup group) {
        NumberAutoGroupValueBean bean = group.getValue().getGroupValue();
        return new AutoNumGroupRule(new Partition(bean.getMin(), bean.getMax(), bean.getGroupInterval()));
    }
}
