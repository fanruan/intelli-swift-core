package com.fr.swift.adaptor.linkage;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BICommonConstants.GROUP;
import com.finebi.conf.constant.BIConfConstants.CONF.COLUMN;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.constant.BIDesignConstants.DESIGN;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.date.DateWidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.TypeGroupBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupNodeBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.number.custom.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.number.NumberWidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.visitor.WidgetBeanToFineWidgetVisitor;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBeanValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.DetailJumpClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.DrillSequence;
import com.finebi.conf.internalimp.dashboard.widget.filter.JumpClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.TableJumpClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetGlobalFilterBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.service.engine.table.EngineTableManager;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionDrill;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.engine.utils.StringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.RelationSourceFactory;
import com.fr.swift.adaptor.transformer.filter.date.DateUtils;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
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
    private static final WidgetBeanToFineWidgetVisitor VISITOR = new WidgetBeanToFineWidgetVisitor();

    /**
     * 计算被联动过滤信息
     *
     * @param filterInfos
     * @return
     */
    private static TableWidgetBean handleClickItem(String tableName, WidgetBean widgetBean, List<ClickValueItem> clickValueItems, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) throws Exception {
        if (null == widgetBean) {
            return null;
        }
        if (!(widgetBean instanceof TableWidgetBean)) {
            Crasher.crash("WidgetBean must instance of " + TableWidgetBean.class.getName() + " but got " + widgetBean.getClass().getName());
        }

        TableWidget fromWidget = (TableWidget) widgetBean.accept(VISITOR);
        String fromTableName = fromWidget.getTableName();

        if (ComparatorUtils.equals(fromTableName, tableName)) {
            handleOneTableFilter(fromWidget, clickValueItems, filterInfos);
        } else {
            handleRelationFilter(tableName, fromWidget, clickValueItems, filterInfos, primary, foreign);
        }
        return fromWidget.getValue();
    }

    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos) throws Exception {
        return handleClickItem(tableName, widgetLinkItem, filterInfos, null, null);
    }

    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) throws Exception {
        ClickValue clicked = widgetLinkItem.getClicked();
        if (clicked == null) {
            return null;
        }
        return handleClickItem(tableName, widgetLinkItem.getWidget(), clicked.getValue(), filterInfos, primary, foreign);
    }

    public static void handleCrossTempletClick(String tableName, WidgetGlobalFilterBean globalBean, List<FilterInfo> filterInfos) throws Exception {
        handleCrossTempletClick(tableName, globalBean, filterInfos, null, null);
    }

    public static void handleCrossTempletClick(String tableName, WidgetGlobalFilterBean globalBean, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) throws Exception {
        JumpClickValue click = globalBean.getClicked();
        if (click == null) {
            return;
        }
        TableWidgetBean srcWidget = globalBean.getLinkedWidget();

        switch (click.getType()) {
            case 1:
                List<ClickValueItem> clicks = ((TableJumpClickValue) click).getValue();
                handleClickItem(tableName, srcWidget, clicks, filterInfos, primary, foreign);
                return;
            case 4:
                DetailJumpClickValue detailClick = (DetailJumpClickValue) click;
                int rowCount = (detailClick.getPageCount() - 1) * DESIGN.DEFAULT_PAGE_ROW_SIZE + detailClick.getRowIndex();

                List<WidgetDimensionBean> dimensionBeans = new ArrayList<WidgetDimensionBean>(srcWidget.getDimensions().values());
                Dimension[] dims = new DetailDimension[dimensionBeans.size()];
                int i = 0;
                SourceKey sourceKey = null;
                for (WidgetDimensionBean dimensionBean : dimensionBeans) {
                    String fieldId = dimensionBean.getFieldId();
                    if (sourceKey == null) {
                        sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
                    }
                    dims[i] = new DetailDimension(i, sourceKey, new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)), null, null, null);
                    i++;
                }
//                QueryInfo queryInfo = new DetailQueryInfo(new AllCursor(), click.getdId(), dims, sourceKey, null, null, null, null);
                QueryInfo queryInfo = null;
                SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
                int cursor = 0;
                while (resultSet.next()) {
                    Row row = resultSet.getRowData();
                    if (cursor++ < rowCount) {
                        continue;
                    }
                    for (int j = 0; j < dims.length; j++) {
                        filterInfos.add(dealFilterInfo(dims[j].getColumnKey(), row.getValue(j).toString(), dimensionBeans.get(j)));
                    }
                    return;
                }
            default:
        }
    }

    private static void handleOneTableFilter(TableWidget fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) throws Exception {
        TableWidgetBean fromBean = fromWidget.getValue();
        if (null != clickedList) {
            for (ClickValueItem clickValueItem : clickedList) {
                String value = clickValueItem.getText();
                WidgetDimensionBean bean = fromBean.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                FilterInfo info = dealFilterInfo(columnKey, value, bean);
                if (null != info) {
                    filterInfos.add(info);
                }

            }
        }
        dealWithDrill(filterInfos, fromWidget, null);
        dealFieldFilter(fromWidget.getTableName(), fromBean.getNewFields(), filterInfos);
    }

    private static RelationSource dealWithCustomRelation(String primaryTable, String foreignTable, Dimension[] primary, String[] foreign) throws Exception {
        EngineTableManager tableManager = fineConfManageCenter.getTableProvider().get(FineEngineType.Cube);
        SourceKey primarySource = DataSourceFactory.getDataSourceInCache(tableManager.getSingleTable(primaryTable)).getSourceKey();
        SourceKey foreignSource = DataSourceFactory.getDataSourceInCache(tableManager.getSingleTable(foreignTable)).getSourceKey();
        List<String> primaryFields = new ArrayList<String>();
        for (Dimension dimension : primary) {
            primaryFields.add(dimension.getColumnKey().getName());
        }
        List<String> foreignFields = Arrays.asList(foreign);
        return new RelationSourceImpl(primarySource, foreignSource, primaryFields, foreignFields);
    }

    private static void handleRelationFilter(String table, TableWidget fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos, Dimension[] primary, String[] foreign) throws Exception {
        TableWidgetBean fromBean = fromWidget.getValue();
        if (null != clickedList) {
            RelationSource relationSource = dealRelationSource(fromWidget.getTableName(), table, primary, foreign);
            for (ClickValueItem clickValueItem : clickedList) {
                String value = clickValueItem.getText();
                WidgetDimensionBean bean = fromBean.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                columnKey.setRelation(relationSource);
                FilterInfo info = dealFilterInfo(columnKey, value, bean);
                if (null != info) {
                    filterInfos.add(info);
                }
            }
            dealWithDrill(filterInfos, fromWidget, relationSource);
        }
        dealFieldFilter(fromWidget.getTableName(), fromBean.getNewFields(), filterInfos);
    }


    public static FilterInfo dealFilterInfo(ColumnKey columnKey, String value, int columnType) {
        switch (columnType) {
            case COLUMN.DATE: {
                return createDateFilter(columnKey, value, GROUP.YMD);
            }
            case COLUMN.NUMBER: {
                Set<Double> values = new HashSet<Double>();
                values.add(Double.parseDouble(value));
                return new SwiftDetailFilterInfo<Set<Double>>(columnKey, values, SwiftDetailFilterType.NUMBER_CONTAIN);
            }
            default: {
                Set<String> values = new HashSet<String>();
                values.add(value);
                return new SwiftDetailFilterInfo<Set<String>>(columnKey, values, SwiftDetailFilterType.STRING_IN);
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
                    List<NumberCustomGroupNodeBean> nodeBeans = valueBean.getGroupNodes();
                    NumberCustomGroupNodeBean filterNode = null;
                    for (NumberCustomGroupNodeBean node : nodeBeans) {
                        if (ComparatorUtils.equals(node.getGroupName(), value)) {
                            filterNode = node;
                            break;
                        }
                    }
                    SwiftNumberInRangeFilterValue numberValue = new SwiftNumberInRangeFilterValue();
                    if (null != filterNode) {
                        numberValue.setMax(filterNode.getMax());
                        numberValue.setMin(filterNode.getMin());
                        numberValue.setMaxIncluded(filterNode.isCloseMax());
                        numberValue.setMinIncluded(filterNode.isCloseMin());
                    } else {
                        numberValue.setMax(valueBean.getMax());
                        numberValue.setMin(valueBean.getMin());
                        numberValue.setMinIncluded(true);
                        numberValue.setMaxIncluded(true);
                    }
                    return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, numberValue, SwiftDetailFilterType.NUMBER_IN_RANGE);
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

    private static void dealFieldFilter(String tableName, List<WidgetBeanField> fields, List<FilterInfo> filterInfos) {
        if (null != fields && !fields.isEmpty()) {
            for (WidgetBeanField field : fields) {
                ArrayList<FilterBean> filterBeans = new ArrayList<FilterBean>();
                if (null != field.getFilter()) {
                    filterBeans.add(field.getFilter());
                }
                if (null != field.getDetailFilter()) {
                    filterBeans.add(field.getDetailFilter());
                }
                if (!filterBeans.isEmpty()) {
                    filterInfos.add(FilterInfoFactory.transformFilterBean(tableName, filterBeans, new ArrayList<Segment>()));
                }
            }
        }
    }

    public static void dealWithDrill(List<FilterInfo> filterInfoList, AbstractTableWidget widget, RelationSource relationSource) throws Exception {
        for (FineDimension fineDimension : widget.getDimensionList()) {
            FineDimensionDrill drill = fineDimension.getDimensionDrill();
            if (drill != null) {
                List<DrillSequence> list = drill.getDrillSequence();
                for (DrillSequence sequence : list) {
                    String value = sequence.getValue();
                    if (StringUtils.isEmpty(value)) {
                        continue;
                    }
                    String columnName = BusinessTableUtils.getFieldNameByFieldId(sequence.getFrom());
                    WidgetBeanField field = widget.getFieldByFieldId(sequence.getFrom());
                    WidgetDimensionBean bean;
                    switch (field.getType()) {
                        case BICommonConstants.COLUMN.DATE:
                            bean = new DateWidgetDimensionBean();
                            TypeGroupBean groupBean = new TypeGroupBean();
                            groupBean.setType(BICommonConstants.GROUP.YMD);
                            bean.setGroup(groupBean);
                            break;
                        case BICommonConstants.COLUMN.NUMBER:
                            bean = new NumberWidgetDimensionBean();
                            break;
                        default:
                            bean = new WidgetDimensionBean();
                    }
                    ColumnKey columnKey = new ColumnKey(columnName);
                    columnKey.setRelation(relationSource);
                    FilterInfo info = dealFilterInfo(columnKey, value, bean);
                    if (null != info) {
                        filterInfoList.add(info);
                    }
                }

            }
        }
    }

    private static RelationSource dealRelationSource(String primaryTable, String foreignTable, Dimension[] primary, String[] foreign) {
        if (ComparatorUtils.equals(primaryTable, foreignTable)) {
            return null;
        }
        EngineRelationPathManager manager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);

        List<FineBusinessTableRelationPath> relationPaths = new ArrayList<FineBusinessTableRelationPath>();
        try {
            relationPaths.addAll(manager.getRelationPaths(primaryTable, foreignTable));
        } catch (FineEngineException e) {
            Crasher.crash("get relation paths error: ", e);
        }
        RelationSource relationSource = null;
        if (relationPaths.isEmpty()) {
            if (null == primary || null == foreign) {
                Crasher.crash(String.format("can not find relation paths between %s and %s!", primaryTable, foreignTable));
            }
            try {
                relationSource = dealWithCustomRelation(primaryTable, foreignTable, primary, foreign);
            } catch (Exception e) {
                Crasher.crash(String.format("create  relation between %s and %s error!", primaryTable, foreignTable), e);
            }
        } else {
            relationSource = RelationSourceFactory.transformRelationSourcesFromPath(relationPaths.get(0));
        }
        return relationSource;
    }
}
