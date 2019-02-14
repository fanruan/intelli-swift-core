package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.exception.FineAnalysisOperationUnSafe;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.EmptyAddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.AccumulativeItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.AddAllAccumulativeValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.group.GroupAccumulativeValue;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.AddAllValueColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.AllValueItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.group.GroupAllValueValue;
import com.finebi.conf.internalimp.analysis.bean.operator.add.expression.AddExpressionValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueItem;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.AddNumberCustomGroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberCustomGroupValueNodeBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.string.AddStringCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.string.StringCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.AddFieldRankColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.AddFieldRankColumnItem;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.group.GroupRankValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.timediff.TimeDiffValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.timediff.TimeDiffValueItem;
import com.finebi.conf.internalimp.analysis.bean.operator.group.CustomGroupValueItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.custom.CustomGroupValueContent;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.AggregatorAdaptor;
import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.adaptor.transformer.EtlAdaptor;
import com.fr.swift.adaptor.widget.group.GroupTypeAdaptor;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.date.GetFromDateOperator;
import com.fr.swift.source.etl.datediff.DateDiffOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.source.etl.group.GroupAssignmentOperator;
import com.fr.swift.source.etl.group.GroupNumericOperator;
import com.fr.swift.source.etl.group.RestrictRange;
import com.fr.swift.source.etl.group.SingleGroup;
import com.fr.swift.source.etl.rowcal.accumulate.AccumulateRowOperator;
import com.fr.swift.source.etl.rowcal.alldata.AllDataRowCalculatorOperator;
import com.fr.swift.source.etl.rowcal.rank.RankRowOperator;
import com.fr.swift.source.etl.utils.FormulaUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class AddColumnAdaptor {
    /**
     * fieldType为将被转换的字段类型
     */
    private static ColumnTypeConstants.ColumnType getColumnType(int fieldType) {
        if (fieldType == BICommonConstants.FORMULA_GENERATE_TYPE.DATE) {
            return ColumnTypeConstants.ColumnType.DATE;
        } else if (fieldType == BICommonConstants.FORMULA_GENERATE_TYPE.NUMBER) {
            return ColumnTypeConstants.ColumnType.NUMBER;
        } else {
            return ColumnTypeConstants.ColumnType.STRING;
        }
    }

    private static ColumnFormulaOperator getColumnFormulaOperator(AddNewColumnValueBean value, DataSource source) {
        String expression = ((AddExpressionValueBean) value).getValue();
        int fieldType = ((AddExpressionValueBean) value).getFieldType();
        if (fieldType != BICommonConstants.FORMULA_GENERATE_TYPE.AUTO) {
            return new ColumnFormulaOperator(value.getName(), getColumnType(fieldType), expression);
        }
        return new ColumnFormulaOperator(value.getName(), FormulaUtils.getColumnType(source.getMetadata(), expression), expression);
    }

    private static AccumulateRowOperator getAccumulateRowOperator(AddNewColumnValueBean value) {
        AccumulativeItemBean tempBean = ((AddAllAccumulativeValueBean) value).getValue();
        String columnName = value.getName();
        ColumnKey columnKey = new ColumnKey(tempBean.getOrigin());
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.NOT_IN_GROUP) {
            return new AccumulateRowOperator(columnKey, columnName, null);
        } else {
            List<String> selects = ((GroupAccumulativeValue) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < selects.size(); i++) {
                dimensions[i] = new ColumnKey(selects.get(i));
            }
            return new AccumulateRowOperator(columnKey, columnName, dimensions);
        }
    }

    private static AllDataRowCalculatorOperator getAllDataRowCalculatorOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        AllValueItemBean tempBean = ((AddAllValueColumnBean) value).getValue();
        String columnKey = tempBean.getOrigin();
        int summary = tempBean.getSummary();
        AggregatorType aggregatorType = AggregatorAdaptor.transformAllValuesAggregatorType(summary);
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.NOT_IN_GROUP) {
            return new AllDataRowCalculatorOperator(columnName, ColumnTypeAdaptor.adaptColumnType(32), columnKey, null, aggregatorType);
        } else {
            List<String> selects = ((GroupAllValueValue) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new ColumnKey(selects.get(i));
            }
            return new AllDataRowCalculatorOperator(columnName, ColumnTypeAdaptor.adaptColumnType(32), columnKey, dimensions, aggregatorType);
        }
    }

    private static RankRowOperator getRankRowOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        AddFieldRankColumnItem tempBean = ((AddFieldRankColumnBean) value).getValue();
        ColumnKey columnKey = new ColumnKey(tempBean.getOrigin());
        //nice job! foundation
        SortType sortType = tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC || tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC_IN_GROUP ? SortType.ASC : SortType.DESC;
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC_IN_GROUP) {
            List<String> selects = ((GroupRankValueBean) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new ColumnKey(selects.get(0));
            }
            return new RankRowOperator(columnName, sortType, columnKey, dimensions);
        } else {
            return new RankRowOperator(columnName, sortType, columnKey, null);
        }
    }

    private static GetFromDateOperator getFromDataOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        GetFieldTimeValueItem tempBean = ((GetFieldTimeValueBean) value).getValue();
        String fieldName = tempBean.getFieldName();
        GroupType type = GroupTypeAdaptor.adaptDateUnit(tempBean.getUnit());
        return new GetFromDateOperator(fieldName, type, columnName);
    }

    private static DateDiffOperator getDateDiffOperator(AddNewColumnValueBean value) {
        TimeDiffValueItem tempBean = ((TimeDiffValueBean) value).getValue();
        String columnName = value.getName();
        String field1 = tempBean.getMinuend();
        String field2 = tempBean.getMinus();
        return new DateDiffOperator(field1, field2, GroupTypeAdaptor.adaptDateGapUnit(tempBean.getUnit()), columnName);
    }

    private static GroupAssignmentOperator getAutoGroupOperator(AddNewColumnValueBean value) {
        StringCustomGroupValueBean bean = ((AddStringCustomGroupValueBean) value).getValue();
        String columnName = value.getName();
        String useOther = bean.getUseOther();
        String field = bean.getField();
        List<CustomGroupValueContent> details = bean.getDetails();
        List<SingleGroup> group = new ArrayList<SingleGroup>();
        for (CustomGroupValueContent content : details) {
            String id = content.getId();
            String name = content.getValue();
            List<CustomGroupValueItemBean> itemBean = content.getContent();
            List<String> dataList = new ArrayList<String>();
            for (CustomGroupValueItemBean tempValue : itemBean) {
                dataList.add(tempValue.getValue());
            }
            SingleGroup singleGroup = new SingleGroup();
            singleGroup.setName(name);
            singleGroup.setList(dataList);
            group.add(singleGroup);
        }
        return new GroupAssignmentOperator(columnName, useOther, new ColumnKey(field), group);
    }

    private static GroupNumericOperator getGroupNumericOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        NumberCustomGroupValueBean bean = ((AddNumberCustomGroupBean) value).getValue();
        String field = bean.getField();
        double max = Double.parseDouble(bean.getMax());
        double min = Double.parseDouble(bean.getMin());
        String other = bean.getUseOther();
        List<NumberCustomGroupValueNodeBean> nodes = bean.getNodes();
        Iterator<NumberCustomGroupValueNodeBean> iterator = nodes.iterator();
        List<RestrictRange> list = new ArrayList<RestrictRange>();
        while (iterator.hasNext()) {
            NumberCustomGroupValueNodeBean nodeBean = iterator.next();
            boolean closemax = nodeBean.isClosemax();
            boolean closemin = nodeBean.isClosemin();
            String groupName = nodeBean.getGroupName();
            double nodeMax = Double.parseDouble(nodeBean.getMax());
            double nodeMin = Double.parseDouble(nodeBean.getMin());
            boolean valid = nodeBean.isValid();
            RestrictRange restrictRange = new RestrictRange(closemax, closemin, groupName, field, nodeMax, nodeMin, valid);
            list.add(restrictRange);
        }
        return new GroupNumericOperator(columnName,
                new ColumnKey(field), max, min, other, list);
    }

    public static AbstractOperator fromAddNewColumnBean(AddNewColumnBean bean, FineBusinessTable table) throws Exception {
        if (bean.getValue() instanceof EmptyAddNewColumnBean) {
            throw new FineAnalysisOperationUnSafe("");
        }

        AddNewColumnValueBean value = bean.getValue();
        DataSource source = EtlAdaptor.adaptEtlDataSource(((FineAnalysisTableImpl) table).getBaseTable());
        switch (value.getType()) {
            case BIConfConstants.CONF.ADD_COLUMN.FORMULA.TYPE: {
                return getColumnFormulaOperator(value, source);
            }
            case BIConfConstants.CONF.ADD_COLUMN.ACCUMULATIVE_VALUE.TYPE: {
                return getAccumulateRowOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.TYPE: {
                return getAllDataRowCalculatorOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.RANKING.TYPE: {
                return getRankRowOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.TIME.TYPE: {
                return getFromDataOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.TIME_GAP.TYPE: {
                return getDateDiffOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.GROUP.TYPE_STRING: {
                return getAutoGroupOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.GROUP.TYPE_NUMBER_CUSTOM: {
                return getGroupNumericOperator(value);
            }
            default:
        }
        return null;
    }
}