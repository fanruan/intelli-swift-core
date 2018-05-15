package com.fr.swift.adaptor.linkage;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.service.engine.table.EngineTableManager;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.RelationSourceFactory;
import com.fr.swift.adaptor.transformer.date.DateUtils;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/4/28
 */
public class LinkageAdaptor {
    private static final FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");

    /**
     * 计算被联动过滤信息
     *
     * @param filterInfos
     * @return
     */
    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) {
        WidgetBean widgetBean = widgetLinkItem.getWidget();
        if (null == widgetBean) {
            return null;
        }
        if (!(widgetBean instanceof TableWidgetBean)) {
            Crasher.crash("WidgetBean must instance of " + TableWidgetBean.class.getName() + " but got " + widgetBean.getClass().getName());
        }
        TableWidgetBean fromWidget = (TableWidgetBean) widgetBean;
        String fromTableName = fromWidget.getTableName();

        ClickValue clickValue = widgetLinkItem.getClicked();
        if (null != clickValue) {
            List<ClickValueItem> clickedList = clickValue.getValue();
            if (ComparatorUtils.equals(fromTableName, tableName)) {
                handleOneTableFilter(fromWidget, clickedList, filterInfos);
            } else {
                handleRelationFilter(tableName, fromWidget, clickedList, filterInfos, primary, foreign);
            }
        }
        return fromWidget;
    }

    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos) {
        return handleClickItem(tableName, widgetLinkItem, filterInfos, null, null);
    }

    private static void handleOneTableFilter(TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) {
        if (null != clickedList) {
            for (ClickValueItem clickValueItem : clickedList) {
                String value = clickValueItem.getText();

                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                FilterInfo info = dealFilterInfo(columnKey, value, bean);
                if (null != info) {
                    filterInfos.add(info);
                }

            }
        }
    }

    public static RelationSource dealWithCustomRelation(String primaryTable, String foreignTable, Dimension[] primary, String[] foreign) throws Exception {
        EngineTableManager tableManager = fineConfManageCenter.getTableProvider().get(FineEngineType.Cube);
        SourceKey primarySource = DataSourceFactory.getDataSource(tableManager.getSingleTable(primaryTable)).getSourceKey();
        SourceKey foreignSource = DataSourceFactory.getDataSource(tableManager.getSingleTable(foreignTable)).getSourceKey();
        List<String> primaryFields = new ArrayList<String>();
        for (Dimension dimension : primary) {
            primaryFields.add(dimension.getColumnKey().getName());
        }
        List<String> foreignFields = Arrays.asList(foreign);
        return new RelationSourceImpl(primarySource, foreignSource, primaryFields, foreignFields);
    }

    private static void handleRelationFilter(String table, TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) {
        if (null != clickedList) {
            EngineRelationPathManager manager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);

            List<FineBusinessTableRelationPath> relationPaths = new ArrayList<FineBusinessTableRelationPath>();
            try {
                relationPaths.addAll(manager.getRelationPaths(fromWidget.getTableName(), table));
                relationPaths.addAll(manager.getRelationPaths(table, fromWidget.getTableName()));
            } catch (FineEngineException e) {
                Crasher.crash("get relation paths error: ", e);
            }
            RelationSource relationSource = null;
            if (relationPaths.isEmpty()) {
                if (null == primary || null == foreign) {
                    Crasher.crash(String.format("can not find relation paths between %s and %s!", table, fromWidget.getTableName()));
                }
                try {
                    relationSource = dealWithCustomRelation(fromWidget.getTableName(), table, primary, foreign);
                } catch (Exception e) {
                    Crasher.crash(String.format("create  relation between %s and %s error!", table, fromWidget.getTableName()), e);
                }
            } else {
                relationSource = RelationSourceFactory.transformRelationSourcesFromPath(relationPaths.get(0));
            }
            for (int i = 0; i < clickedList.size(); i++) {
                ClickValueItem clickValueItem = clickedList.get(i);
                String value = clickValueItem.getText();
                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                columnKey.setRelation(relationSource);
                FilterInfo info = dealFilterInfo(columnKey, value, bean);
                if (null != info) {
                    filterInfos.add(info);
                }
            }
        }
    }

    public static FilterInfo dealFilterInfo(ColumnKey columnKey, String value, WidgetDimensionBean bean) {

        switch (bean.getType()) {
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.DATE:
                return createDateFilter(columnKey, value, bean.getGroup().getType());
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.NUMBER:
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.TRANSFORM_FROM_NUMBER:
                return createNumberFilter(columnKey, bean, value);
            default:
                Set<String> values = new HashSet<String>();
                values.add(value);
                return new SwiftDetailFilterInfo<Set<String>>(columnKey, values, SwiftDetailFilterType.STRING_IN);
        }

    }

    private static FilterInfo createNumberFilter(ColumnKey columnKey, WidgetDimensionBean bean, String value) {
        switch (bean.getGroup().getType()) {
            case BIDesignConstants.DESIGN.GROUP.CUSTOM_NUMBER_GROUP:
                NumberCustomGroupValueBean valueBean = ((NumberCustomGroupBean) bean.getGroup()).getGroupValue();
                if (null != valueBean) {
                    NumberValue numberValue = new NumberValue();
                    numberValue.setMax(valueBean.getMax());
                    numberValue.setMin(valueBean.getMin());
                    numberValue.setClosemax(false);
                    numberValue.setClosemin(true);
                    return new SwiftDetailFilterInfo<NumberValue>(columnKey, numberValue, SwiftDetailFilterType.NUMBER_IN_RANGE);
                }
                break;
            default:
                Set<Double> values = new HashSet<Double>();
                values.add(Double.parseDouble(value));
                return new SwiftDetailFilterInfo<Set<Double>>(columnKey, values, SwiftDetailFilterType.NUMBER_CONTAIN);
        }
        return null;
    }

    private static FilterInfo createDateFilter(ColumnKey columnKey, String value, int groupType) {
        Calendar calendar = Calendar.getInstance();
        if (groupType > BICommonConstants.GROUP.SECOND || groupType == BICommonConstants.GROUP.YMD) {
            calendar.setTimeInMillis(Long.valueOf(value));
        }
        DateFilterBean bean = createStaticDateBean(calendar, groupType, value);
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        long[] range = DateUtils.rangeOfDateFilterBean(bean);
        filterValue.setStart(range[0]);
        filterValue.setEnd(range[1]);
        return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey, filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
    }

    private static DateFilterBean createStaticDateBean(Calendar calendar, int type, String value) {
        DateStaticFilterBeanValue beanValue = new DateStaticFilterBeanValue();
        DateStaticFilterBean bean = new DateStaticFilterBean();
        // 如果是Y/M/S/D/HOUR/MIMUTE/SECOND 传过来的不是时间戳就是值
        switch (type) {
            case BICommonConstants.GROUP.Y:
                beanValue.setYear(value);
                break;
            case BICommonConstants.GROUP.M:
                beanValue.setMonth(value);
                break;
            case BICommonConstants.GROUP.D:
                beanValue.setDay(value);
                break;
            case BICommonConstants.GROUP.S:
                beanValue.setQuarter(value);
                break;
            case BICommonConstants.GROUP.HOUR:
                beanValue.setHour(value);
                break;
            case BICommonConstants.GROUP.MINUTE:
                beanValue.setMinute(value);
                break;
            case BICommonConstants.GROUP.SECOND:
                beanValue.setSecond(value);
                break;
            case BICommonConstants.GROUP.YS:
                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
                beanValue.setQuarter(String.valueOf(1 + calendar.get(Calendar.MONTH) / 3));
                break;
            case BICommonConstants.GROUP.YM:
                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
                break;
//            case BICommonConstants.GROUP.YMD:
//                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
//                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
//                beanValue.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//                break;
//            case BICommonConstants.GROUP.YMDH:
//                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
//                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
//                beanValue.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//                beanValue.setHour(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                break;
//            case BICommonConstants.GROUP.YMDHM:
//                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
//                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
//                beanValue.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//                beanValue.setHour(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                beanValue.setMinute(String.valueOf(calendar.get(Calendar.MINUTE)));
//                break;
//            case BICommonConstants.GROUP.YMDHMS:
//                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
//                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
//                beanValue.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//                beanValue.setHour(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                beanValue.setMinute(String.valueOf(calendar.get(Calendar.MINUTE)));
//                beanValue.setSecond(String.valueOf(calendar.get(Calendar.SECOND)));
//                break;
            default:
                beanValue.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
                beanValue.setMonth(String.valueOf(1 + calendar.get(Calendar.MONTH)));
                beanValue.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                break;

        }
        bean.setValue(beanValue);
        return bean;
    }
}
