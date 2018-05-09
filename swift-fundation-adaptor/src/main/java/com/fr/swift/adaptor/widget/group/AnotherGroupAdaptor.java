package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.constant.BIConfConstants.CONF.GROUP.GROUP_TYPE;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberCustomGroupValueNodeBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.CustomGroupValueItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.custom.CustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.custom.CustomGroupValueContent;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupRule;
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
 * nice job! bi-foundation
 */
class AnotherGroupAdaptor {
    static Group adapt(GroupCustomGroupValueBean bean) {
        CustomGroupValueBean group = bean.getGroup();

        GroupRule groupRule;
        switch (group.getType()) {
            case GROUP_TYPE.AUTO:
                groupRule = newAutoRule((com.finebi.conf.internalimp.analysis.bean.operator.group.custom.NumberAutoGroupValueBean) group);
                break;
            case GROUP_TYPE.CUSTOM:
                groupRule = newCustomNumberRule((com.finebi.conf.internalimp.analysis.bean.operator.group.custom.NumberCustomGroupValueBean) group);
                break;
            case GROUP_TYPE.CUSTOM_STRING:
                groupRule = newCustomRule((com.finebi.conf.internalimp.analysis.bean.operator.group.custom.StringCustomGroupValueBean) group);
                break;
            default:
                groupRule = new NoGroupRule();
        }
        return Groups.newGroup(groupRule);
    }

    private static boolean anotherIsEmptyStringGroup(CustomGroupValueContent stringGroup) {
        return stringGroup.getContent().isEmpty();
    }

    private static GroupRule newCustomRule(com.finebi.conf.internalimp.analysis.bean.operator.group.custom.StringCustomGroupValueBean bean) {
        List<StringGroup> stringGroups = new ArrayList<StringGroup>();
        for (CustomGroupValueContent detailsBean : bean.getDetails()) {
            if (anotherIsEmptyStringGroup(detailsBean)) {
                continue;
            }

            String groupName = detailsBean.getValue();
            List<String> values = new ArrayList<String>();
            for (CustomGroupValueItemBean itemBean : detailsBean.getContent()) {
                values.add(itemBean.getValue());
            }
            stringGroups.add(new StringGroup(groupName, values));
        }

        return new CustomStrGroupRule(stringGroups, bean.getUseOther(), false);
    }

    private static GroupRule newCustomNumberRule(com.finebi.conf.internalimp.analysis.bean.operator.group.custom.NumberCustomGroupValueBean groupValue) {
        List<NumberCustomGroupValueNodeBean> beans = groupValue.getNodes();

        List<NumInterval> intervals = new ArrayList<NumInterval>(beans.size());
        for (NumberCustomGroupValueNodeBean bean : beans) {
            intervals.add(new NumInterval(bean.getGroupName(),
                    Double.valueOf(bean.getMin()), bean.isClosemin(),
                    Double.valueOf(bean.getMax()), bean.isClosemax()));
        }

        return new CustomNumGroupRule(intervals, groupValue.getUseOther(), false);
    }

    private static GroupRule newAutoRule(com.finebi.conf.internalimp.analysis.bean.operator.group.custom.NumberAutoGroupValueBean bean) {
        return new AutoNumGroupRule(new Partition(bean.getMin(), bean.getMax(), bean.getGroupInterval()), false);
    }
}