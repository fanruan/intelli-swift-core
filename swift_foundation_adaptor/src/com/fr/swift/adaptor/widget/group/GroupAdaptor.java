package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.constant.BIConfConstants.CONF.GROUP.TYPE;
import com.finebi.conf.constant.BIDesignConstants.DESIGN.SORT;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSelectValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupDoubleValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupSingleValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.auto.NumberAutoGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupNodeBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomDetailsItemBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.string.StringCustomGroupValueBean;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionAutoGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.number.NumberDimensionCustomGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.string.StringDimensionCustomGroup;
import com.finebi.conf.internalimp.dashboard.widget.dimension.sort.DimensionCustomSort;
import com.finebi.conf.internalimp.dashboard.widget.dimension.sort.DimensionFilterCustomSort;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.AutoNumGroupRule;
import com.fr.swift.query.group.impl.AutoNumGroupRule.Partition;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.query.group.impl.CustomSortGroupRule;
import com.fr.swift.query.group.impl.CustomStrGroupRule;
import com.fr.swift.query.group.impl.CustomStrGroupRule.StringGroup;
import com.fr.swift.query.group.impl.NoGroupRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/21
 */
public class GroupAdaptor {
    public static Group adaptGroup(DimensionSelectValue selectValue) {
        switch (selectValue.getType()) {
            case TYPE.GROUP:
                return AnotherGroupAdaptor.adapt(((GroupCustomGroupValueBean) selectValue));
            case TYPE.SINGLE: {
                // 相同值作为一组，可直接取底层的dict
                GroupType type = GroupTypeAdaptor.adaptSingleValueGroupType(((GroupSingleValueBean) selectValue).getValue());
                return Groups.newGroup(new NoGroupRule(type));
            }
            case TYPE.DOUBLE: {
                // 不知道double是啥, 暂用于日期分组的更多分组
                // 相同值作为一组，可直接取底层的dict
                GroupType type = GroupTypeAdaptor.adaptSingleValueGroupType(((GroupDoubleValueBean) selectValue).getChildValue());
                return Groups.newGroup(new NoGroupRule(type));
            }
            default:
                return Groups.newGroup(new NoGroupRule());
        }
    }

    /**
     * nice job! foundation
     *
     * @param dimGroup
     * @return
     */
    public static Group adaptDashboardGroup(FineDimensionGroup dimGroup) {
        if (dimGroup == null) {
            return Groups.newGroup(new NoGroupRule());
        }
        GroupType type = GroupTypeAdaptor.adaptDashboardGroup(dimGroup.getType());
        return Groups.newGroup(adaptRule(type, dimGroup));
    }

    public static Group adaptDashboardGroup(FineDimension fineDim) {
        FineDimensionSort fineDimSort = fineDim.getSort();
        Group originGroup = adaptDashboardGroup(fineDim.getGroup());
        if (fineDimSort == null) {
            return originGroup;
        }
        switch (fineDimSort.getType()) {
            case SORT.CUSTOM: {
                List<String> values = ((DimensionCustomSort) fineDimSort).getValue().getDetails();
                if (values == null || values.isEmpty()) {
                    return originGroup;
                }
                List<StringGroup> stringGroups = new ArrayList<StringGroup>();
                for (String value : values) {
                    stringGroups.add(new StringGroup(value, Collections.singletonList(value)));
                }
                return Groups.wrap(originGroup, new CustomSortGroupRule(stringGroups, null));
            }
            case SORT.FILTER_CUSTOM: {
                List<String> values = ((DimensionFilterCustomSort) fineDimSort).getValue().getDetails();
                if (values == null || values.isEmpty()) {
                    return originGroup;
                }

                List<StringGroup> stringGroups = new ArrayList<StringGroup>();
                for (String value : values) {
                    stringGroups.add(new StringGroup(value, Collections.singletonList(value)));
                }
                return Groups.wrap(originGroup, new CustomSortGroupRule(stringGroups, null));
            }
            default:
                return originGroup;
        }
    }

    private static GroupRule adaptRule(GroupType type, FineDimensionGroup dimGroup) {
        switch (type) {
            case AUTO:
                return newAutoRule((NumberDimensionAutoGroup) dimGroup);
            case CUSTOM_NUMBER:
                return newCustomNumberRule((NumberDimensionCustomGroup) dimGroup);
            case CUSTOM:
                return newCustomRule((StringDimensionCustomGroup) dimGroup);
            default:
                return new NoGroupRule(type);
        }
    }

    private static boolean isEmptyStringGroup(StringCustomDetailsBean stringGroup) {
        return stringGroup.getContent().isEmpty();
    }

    private static GroupRule newCustomRule(StringDimensionCustomGroup dimGroup) {
        StringCustomGroupValueBean bean = dimGroup.getValue().getValue();

        List<StringGroup> stringGroups = new ArrayList<StringGroup>();
        for (StringCustomDetailsBean detailsBean : bean.getDetails()) {
            if (isEmptyStringGroup(detailsBean)) {
                continue;
            }

            String groupName = detailsBean.getValue();
            List<String> values = new ArrayList<String>();
            for (StringCustomDetailsItemBean itemBean : detailsBean.getContent()) {
                values.add(itemBean.getValue());
            }
            stringGroups.add(new StringGroup(groupName, values));
        }

        return new CustomStrGroupRule((List) stringGroups, bean.getUseOther());
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

        return new CustomNumGroupRule((List) intervals, groupValue.getUseOther());
    }

    private static GroupRule newAutoRule(NumberDimensionAutoGroup group) {
        NumberAutoGroupValueBean bean = group.getValue().getGroupValue();
        return new AutoNumGroupRule(new Partition(bean.getMin(), bean.getMax(), bean.getGroupInterval()));
    }
}