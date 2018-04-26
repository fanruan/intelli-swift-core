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
import com.fr.swift.source.etl.groupsum.SumByGroupDimension;
import com.fr.swift.source.etl.groupsum.SumByGroupOperator;
import com.fr.swift.source.etl.groupsum.SumByGroupTarget;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class GroupSumAdaptor {
    public static SumByGroupOperator fromSumByGroupBean(GroupBean bean) {
        GroupValueBean valueBean = bean.getValue();
        Map<String, DimensionValueBean> dimensionBean = valueBean.getDimensions();
        ViewBean viewBean = valueBean.getView();
        List<String> dimensions = viewBean.getDimension();
        List<String> views = viewBean.getViews();
        if (dimensionBean.isEmpty()) {
            return null;
        }

        SumByGroupDimension[] groupDimensions = null;
        SumByGroupTarget[] groupTargets = new SumByGroupTarget[0];
        if (null != views) {
            groupTargets = new SumByGroupTarget[views.size()];
        }
        if (null != dimensions) {
            groupDimensions = new SumByGroupDimension[dimensions.size()];
        }
        for (int i = 0; i < groupDimensions.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(dimensions.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            List<DimensionSelectValue> value = tempBean.getValue();
            SumByGroupDimension sumByGroupDimension = new SumByGroupDimension();
            sumByGroupDimension.setGroup(GroupAdaptor.adaptGroup(value.get(0)));
            sumByGroupDimension.setName(srcValue.getFieldName());
            sumByGroupDimension.setNameText(tempBean.getName());
            groupDimensions[i] = sumByGroupDimension;
        }
        for (int i = 0; i < groupTargets.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(views.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            SumByGroupTarget sumByGroupTarget = new SumByGroupTarget();
            sumByGroupTarget.setName(srcValue.getFieldName());
            sumByGroupTarget.setNameText(tempBean.getName());
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
            sumByGroupTarget.setAggregator(AggregatorFactory.createAggregator(aggregatorType));
            sumByGroupTarget.setColumnType(ColumnTypeAdaptor.adaptColumnType(tempBean.getFieldType()));
            if (aggregatorType == AggregatorType.COUNT || aggregatorType == AggregatorType.DISTINCT) {
                sumByGroupTarget.setColumnType(ColumnTypeConstants.ColumnType.NUMBER);
            }
            sumByGroupTarget.setSumType(type);
            groupTargets[i] = sumByGroupTarget;
        }
        return new SumByGroupOperator(groupTargets, groupDimensions);
    }
}