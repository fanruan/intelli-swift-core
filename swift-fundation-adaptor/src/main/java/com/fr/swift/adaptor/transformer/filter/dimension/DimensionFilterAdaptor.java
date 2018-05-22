package com.fr.swift.adaptor.transformer.filter.dimension;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringTopNFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.filter.FineFilter;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.group.GroupTypeAdaptor;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.MatchFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.match.MatchConverterFactory;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/5/16.
 */
public class DimensionFilterAdaptor {

    public static FilterInfo transformDimensionFineFilter(FineDimension dimension) {
        return transformDimensionFineFilter(null, dimension, false, null);
    }

    /**
     * @param tableName           控件的表名，没表名传null或者空字符串
     * @param dimension
     * @param attachTargetFilters 最后一个维度上面要加上指标的过滤
     * @param targets
     * @return
     */
    public static FilterInfo transformDimensionFineFilter(String tableName, FineDimension dimension,
                                                          boolean attachTargetFilters,
                                                          Pair<List<FineTarget>, List<Integer>> targets) {
        List<FilterBean> beans = setFieldIdAndGetFilterBeans(dimension, attachTargetFilters);
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        for (FilterBean bean : beans) {
            AbstractFilterBean filterBean = (AbstractFilterBean) bean;
            // 因为前端不区分明细过滤和结果过滤，直接用通用过滤器平拼在一起的
            if (filterBean != null && targets != null) {
                // 暂时全部是matchFilter
                filterInfoList.add(getResultFilterInfo(tableName, filterBean, dimension, targets));
            } else {
                // 这边给文本下拉控件用的
                filterInfoList.add(getDetailFilterInfo(tableName, filterBean, dimension));
            }
        }
        if (attachTargetFilters && targets != null) {
            for (int i = 0; i < targets.getKey().size(); i++) {
                FineTarget target = targets.getKey().get(i);
                List<FineFilter> targetFilters = target.getFilters();
                if (targetFilters != null) {
                    for (FineFilter filter : targetFilters) {
                        if (filter.getFilterType() == BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION) {
                            continue;
                        }
                        FilterInfo targetFilterInfo = FilterInfoFactory.createFilterInfo(tableName, (AbstractFilterBean) filter.getValue(), new ArrayList<Segment>());
                        filterInfoList.add(new MatchFilterInfo(targetFilterInfo, targets.getValue().get(i), MatchConverterFactory.createConvertor(GroupTypeAdaptor.adaptDashboardGroup(dimension.getGroup().getType()))));
                    }
                }
            }
        }
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    private static FilterInfo getDetailFilterInfo(String tableName, AbstractFilterBean filterBean, FineDimension dimension) {
        int type = filterBean.getFilterType();
        if (type == BICommonConstants.ANALYSIS_FILTER_TYPE.OR || type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND) {
            List<FilterBean> beans = type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND ?
                    ((GeneraAndFilterBean) filterBean).getFilterValue() : ((GeneraOrFilterBean) filterBean).getFilterValue();
            List<FilterInfo> children = new ArrayList<FilterInfo>();
            for (FilterBean bean : beans) {
                FilterInfo info = getDetailFilterInfo(tableName, (AbstractFilterBean) bean, dimension);
                if (info != null) {
                    children.add(info);
                }
            }
            return new GeneralFilterInfo(children, type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND ?
                    GeneralFilterInfo.AND : GeneralFilterInfo.OR);

        }
        filterBean.setFieldId(dimension.getFieldId());
        return FilterInfoFactory.createFilterInfo(tableName, filterBean, new ArrayList<Segment>());
    }

    /**
     * 日期、数值转换过来的维度上面的过滤要转换一下，这边调用yee在联动那边做的转换
     * 这边只加了日期属于不属于的过滤，其他的功能那边也还没好
     *
     * @param filterInfo 要做转换的过滤属性
     * @param dimension
     * @return
     */
    private static FilterInfo getFilterInfoFromConvertedDimension(FilterInfo filterInfo, FineDimension dimension) {
        if (filterInfo instanceof GeneralFilterInfo) {
            List<FilterInfo> infoList = ((GeneralFilterInfo) filterInfo).getChildren();
            List<FilterInfo> convertedInfoList = new ArrayList<FilterInfo>();
            for (FilterInfo info : infoList) {
                FilterInfo converted = getFilterInfoFromConvertedDimension(info, dimension);
                if (converted != null) {
                    convertedInfoList.add(converted);
                }
            }
            return new GeneralFilterInfo(convertedInfoList, ((GeneralFilterInfo) filterInfo).getType());
        }
        SwiftDetailFilterType type = ((SwiftDetailFilterInfo) filterInfo).getType();
        SwiftDetailFilterInfo detailFilterInfo = (SwiftDetailFilterInfo) filterInfo;
        switch (type) {
            case TMP_DATE_BELONG_STRING: {
                List<String> dates = (List<String>) detailFilterInfo.getFilterValue();
                List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
                ColumnKey key = detailFilterInfo.getColumnKey();
                WidgetDimensionBean dimensionBean = (WidgetDimensionBean) dimension.getValue();
                for (String date : dates) {
                    filterInfoList.add(LinkageAdaptor.dealFilterInfo(key, date, dimensionBean));
                }
                return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
            }
            case TMP_DATE_NOT_BELONG_STRING: {
                List<String> dates = (List<String>) detailFilterInfo.getFilterValue();
                List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
                ColumnKey key = detailFilterInfo.getColumnKey();
                WidgetDimensionBean dimensionBean = (WidgetDimensionBean) dimension.getValue();
                for (String date : dates) {
                    SwiftDetailFilterInfo info = (SwiftDetailFilterInfo) LinkageAdaptor.dealFilterInfo(key, date, dimensionBean);
                    info = new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(key, (SwiftDateInRangeFilterValue) info.getFilterValue(),
                            SwiftDetailFilterType.STRING_NOT_IN);
                    filterInfoList.add(info);
                }
                return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
            }
            // TODO: 2018/5/16 维度上面其他要二次转换到的过滤类型，比如日期前端现在只加了属于和不属于
            default:
        }
        return filterInfo;
    }

    private static FilterInfo getResultFilterInfo(String tableName, AbstractFilterBean filterBean,
                                                  FineDimension dimension, Pair<List<FineTarget>, List<Integer>> targets) {
        int type = filterBean.getFilterType();
        if (type == BICommonConstants.ANALYSIS_FILTER_TYPE.OR || type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND) {
            List<FilterBean> beans = type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND ?
                    ((GeneraAndFilterBean) filterBean).getFilterValue() : ((GeneraOrFilterBean) filterBean).getFilterValue();
            List<FilterInfo> children = new ArrayList<FilterInfo>();
            for (FilterBean bean : beans) {
                FilterInfo info = getResultFilterInfo(tableName, (AbstractFilterBean) bean, dimension, targets);
                children.add(info);
            }
            return new GeneralFilterInfo(children, type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND ?
                    GeneralFilterInfo.AND : GeneralFilterInfo.OR);

        }
        SwiftDetailFilterInfo info = (SwiftDetailFilterInfo) FilterInfoFactory.createMatchFilterInfo(filterBean, MatchConverterFactory.createConvertor(GroupTypeAdaptor.adaptDashboardGroup(dimension.getGroup().getType())));
        if (info.getType() == SwiftDetailFilterType.FORMULA) {
            info = new SwiftDetailFilterInfo<Object>(info.getColumnKey(), transformTargetMatchFormula(info.getFilterValue(), targets), info.getType());
            return new MatchFilterInfo(info, getIndex(filterBean.getTargetId(), targets), MatchConverterFactory.createConvertor(GroupTypeAdaptor.adaptDashboardGroup(dimension.getGroup().getType())));
        } else {
            return new MatchFilterInfo(info, getIndex(filterBean.getTargetId(), targets), MatchConverterFactory.createConvertor(GroupTypeAdaptor.adaptDashboardGroup(dimension.getGroup().getType())));
        }
    }

    private static List<FilterBean> setFieldIdAndGetFilterBeans(FineDimension dimension, boolean attachTargetFilters) {
        List<FineFilter> filters = dimension.getFilters();
        List<FilterBean> beans = new ArrayList<FilterBean>();
        if (filters != null) {
            for (FineFilter filter : filters) {
                //nice job! foundation 维度过滤没id，要从维度上设置一下。
                if (filter.getValue() != null) {
                    AbstractFilterBean bean = (AbstractFilterBean) filter.getValue();
                    String fieldId;
                    if (dimension.getWidgetBeanField() != null) {
                        WidgetBeanField field = dimension.getWidgetBeanField();
                        fieldId = StringUtils.isEmpty(field.getSource()) ? field.getId() : field.getSource();
                    } else {
                        fieldId = dimension.getFieldId();
                    }
                    // 如果是generalBean还要递归地设置一下，坑爹！
                    deepSettingFieldId(bean, fieldId);
                    bean = attachTargetFilters ? bean : changeNFilterBySort(bean, dimension.getSort());
                    beans.add(bean);
                }
            }
        }
        return beans;
    }

    private static AbstractFilterBean changeNFilterBySort(AbstractFilterBean bean, FineDimensionSort sort) {
        if (sort != null && sort.getType() == BIDesignConstants.DESIGN.SORT.FILTER_DESC) {
            switch (bean.getFilterType()) {
                case BICommonConstants.ANALYSIS_FILTER_STRING.TOP_N:
                    StringBottomNFilterBean bottomN = new StringBottomNFilterBean();
                    bottomN.setFieldId(bean.getFieldId());
                    bottomN.setFilterValue((Long) bean.getFilterValue());
                    return bottomN;
                case BICommonConstants.ANALYSIS_FILTER_STRING.BOTTOM_N:
                    StringTopNFilterBean topN = new StringTopNFilterBean();
                    topN.setFieldId(bean.getFieldId());
                    topN.setFilterValue((Long) bean.getFilterValue());
                    return topN;
                case BICommonConstants.ANALYSIS_FILTER_TYPE.AND:
                case BICommonConstants.ANALYSIS_FILTER_TYPE.OR:
                    List<FilterBean> children = (List<FilterBean>) bean.getFilterValue();
                    for (int i = 0; i < children.size(); i++) {
                        children.set(i, changeNFilterBySort((AbstractFilterBean) children.get(i), sort));
                    }
                    return bean;
                default:
            }

        }
        return bean;
    }

    private static Object transformTargetMatchFormula(Object value, Pair<List<FineTarget>, List<Integer>> targets) {
        if (value == null) {
            return value;
        }
        String formula = value.toString();
        for (String targetId : FormulaUtils.getRelatedParaNames(formula)) {
            formula = formula.replace(targetId, String.valueOf(getIndex(targetId, targets)));
        }
        return formula;
    }

    public static void deepSettingFieldId(FineDimension dimension) {
        List<FineFilter> fineFilters = dimension.getFilters();
        fineFilters = fineFilters == null ? new ArrayList<FineFilter>(0) : fineFilters;
        String fieldId = dimension.getFieldId();
        for (FineFilter fineFilter : fineFilters) {
            AbstractFilterBean bean = (AbstractFilterBean) fineFilter.getValue();
            deepSettingFieldId(bean, fieldId);
        }
    }

    private static void deepSettingFieldId(AbstractFilterBean bean, String fieldId) {
        List<FilterBean> filterBeans = null;
        if (bean instanceof GeneraAndFilterBean) {
            filterBeans = ((GeneraAndFilterBean) bean).getFilterValue();
        } else if (bean instanceof GeneraOrFilterBean) {
            filterBeans = ((GeneraOrFilterBean) bean).getFilterValue();
        }
        if (filterBeans != null) {
            for (FilterBean b : filterBeans) {
                deepSettingFieldId((AbstractFilterBean) b, fieldId);
            }
        } else {
            bean.setFieldId(fieldId);
        }
    }

    private static int getIndex(String targetId, Pair<List<FineTarget>, List<Integer>> targets) {
        for (int i = 0; i < targets.getKey().size(); i++) {
            if (ComparatorUtils.equals(targetId, targets.getKey().get(i).getId())) {
                return targets.getValue().get(i);
            }
        }
        return -1;
    }

    private static boolean isConvertedDimension(FineDimension dimension) {
        int type = ((WidgetDimensionBean) dimension.getValue()).getType();
        return type == BIDesignConstants.DESIGN.DIMENSION_TYPE.DATE
                || type == BIDesignConstants.DESIGN.DIMENSION_TYPE.TRANSFORM_FROM_NUMBER;
    }
}
