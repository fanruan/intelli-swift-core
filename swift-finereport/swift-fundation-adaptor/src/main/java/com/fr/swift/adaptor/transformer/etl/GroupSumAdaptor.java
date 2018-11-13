package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSelectValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSrcValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupSingleValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.ViewBean;
import com.fr.swift.adaptor.transformer.AggregatorAdaptor;
import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.etl.groupsum.GroupSumDimension;
import com.fr.swift.source.etl.groupsum.GroupSumOperator;
import com.fr.swift.source.etl.groupsum.GroupSumTarget;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class GroupSumAdaptor {
    public static GroupSumOperator fromSumByGroupBean(GroupBean bean) {
        GroupValueBean valueBean = bean.getValue();
        Map<String, DimensionValueBean> dimensionBean = valueBean.getDimensions();
        ViewBean viewBean = valueBean.getView();
        List<String> dimensions = viewBean.getDimension();
        List<String> views = viewBean.getViews();
        if (dimensionBean.isEmpty()) {
            return null;
        }

        GroupSumDimension[] groupDimensions = null;
        GroupSumTarget[] groupTargets = new GroupSumTarget[0];
        if (null != views) {
            groupTargets = new GroupSumTarget[views.size()];
        }
        if (null != dimensions) {
            groupDimensions = new GroupSumDimension[dimensions.size()];
        }
        for (int i = 0; i < groupDimensions.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(dimensions.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            List<DimensionSelectValue> value = tempBean.getValue();
            GroupSumDimension groupSumDimension = new GroupSumDimension();
            groupSumDimension.setGroup(GroupAdaptor.adaptGroup(value.get(0)));
            groupSumDimension.setName(srcValue.getFieldName());
            groupSumDimension.setNameText(tempBean.getName());
            groupDimensions[i] = groupSumDimension;
        }
        for (int i = 0; i < groupTargets.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(views.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            GroupSumTarget groupSumTarget = new GroupSumTarget();
            groupSumTarget.setName(srcValue.getFieldName());
            groupSumTarget.setNameText(tempBean.getName());
            int type = BIConfConstants.CONF.GROUP.NUMBER.SUM;
            switch (tempBean.getValue().get(0).getType()) {
                case BIConfConstants.CONF.GROUP.TYPE.SINGLE:
                    type = ((GroupSingleValueBean) tempBean.getValue().get(0)).getValue();
                    break;
                case BIConfConstants.CONF.GROUP.TYPE.DOUBLE:
                    // TODO
                    break;
                default:
                    //TODO
                    break;
            }
            AggregatorType aggregatorType = AggregatorAdaptor.transformAggregatorType(tempBean.getFieldType(), type);
            groupSumTarget.setAggregator(AggregatorFactory.createAggregator(aggregatorType));
            groupSumTarget.setColumnType(ColumnTypeAdaptor.adaptColumnType(tempBean.getFieldType()));
            if (aggregatorType == AggregatorType.COUNT || aggregatorType == AggregatorType.DISTINCT) {
                groupSumTarget.setColumnType(ColumnTypeConstants.ColumnType.NUMBER);
            }
            groupSumTarget.setSumType(type);
            groupTargets[i] = groupSumTarget;
        }
        return new GroupSumOperator(groupTargets, groupDimensions);
    }
}