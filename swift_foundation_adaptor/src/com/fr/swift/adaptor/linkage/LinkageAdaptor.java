package com.fr.swift.adaptor.linkage;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.RelationSourceFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.column.RelationColumn;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
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
    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos) {
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
                handleRelationFilter(tableName, fromWidget, clickedList, filterInfos);
            }
        }
        return fromWidget;
    }

    private static void handleOneTableFilter(TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) {
        if (null != clickedList) {
            for (ClickValueItem clickValueItem : clickedList) {
                String value = clickValueItem.getText();
                Set<String> values = new HashSet<String>();
                values.add(value);
                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()), values, SwiftDetailFilterType.STRING_IN));
            }
        }
    }

    private static void handleRelationFilter(String table, TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) {
        if (null != clickedList) {
            EngineRelationPathManager manager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);
            List<FineBusinessTableRelationPath> relationPaths = new ArrayList<FineBusinessTableRelationPath>();
            try {
                relationPaths.addAll(manager.getRelationPaths(fromWidget.getTableName(), table));
                relationPaths.addAll(manager.getRelationPaths(table, fromWidget.getTableName()));
            } catch (FineEngineException e) {
                Crasher.crash("get relation paths error: ", e);
            }
            if (relationPaths.isEmpty()) {
                Crasher.crash(String.format("can not find relation paths between %s and %s!", table, fromWidget.getTableName()));
            }
            RelationSource relationSource = RelationSourceFactory.transformRelationSourcesFromPath(relationPaths.get(0));
            boolean isPrimary = true;
            for (int i = 0; i < clickedList.size(); i++) {
                ClickValueItem clickValueItem = clickedList.get(i);
                String value = clickValueItem.getText();
                Set<String> values = new HashSet<String>();
                values.add(value);
                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                if (i == 0) {
                    String fromTableSource = BusinessTableUtils.getSourceIdByFieldId(bean.getFieldId());
                    isPrimary = ComparatorUtils.equals(relationSource.getPrimarySource().getId(), fromTableSource);
                }
                if (isPrimary) {
                    handlePrimaryValues(relationSource, BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()), values, filterInfos);
                } else {
                    handleForeignValues(relationSource, BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()), values, filterInfos);
                }
            }
        }
    }

    private static void handlePrimaryValues(RelationSource relationSource, String fieldName, Set<String> value, List<FilterInfo> filterInfos) {
        SourceKey primary = relationSource.getPrimarySource();
        SourceKey foreign = relationSource.getForeignSource();
        List<Segment> primarySegments = LocalSegmentProvider.getInstance().getSegment(primary);
        List<Segment> foreignSegments = LocalSegmentProvider.getInstance().getSegment(foreign);
        ColumnKey key = new ColumnKey(fieldName);
        key.setRelation(relationSource);
        List<RelationColumn.KeyRow> rows = new ArrayList<RelationColumn.KeyRow>();
        for (Segment foreignSegment : foreignSegments) {
            RelationIndex index = foreignSegment.getRelation(RelationPathHelper.convert2CubeRelationPath(relationSource));

            RelationColumn column = new RelationColumn(index, primarySegments, key);
            rows.addAll(column.getRows(value));
            column.release();
        }

        List<String> foreignFields = relationSource.getForeignFields();
        for (int i = 0; i < foreignFields.size(); i++) {
            Set<String> set = new HashSet<String>();
            for (RelationColumn.KeyRow row : rows) {
                set.add(row.getData()[i].toString());
            }
            filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(foreignFields.get(i), set, SwiftDetailFilterType.STRING_IN));
        }

    }

    private static void handleForeignValues(RelationSource relationSource, String fieldName, Set<String> values, List<FilterInfo> filterInfos) {
        SourceKey foreign = relationSource.getForeignSource();
        List<String> primaryFields = relationSource.getPrimaryFields();
        List<String> foreignFields = relationSource.getForeignFields();
        SourceKey primary = relationSource.getPrimarySource();
        List<Segment> primarySegments = LocalSegmentProvider.getInstance().getSegment(primary);
        List<Segment> foreignSegments = LocalSegmentProvider.getInstance().getSegment(foreign);
        final ColumnKey key = new ColumnKey(fieldName);
        key.setRelation(relationSource);
        final int size = foreignFields.size();
        final Set<String> filterSet[] = new Set[size];
        for (Segment foreignSegment : foreignSegments) {
            Column column = foreignSegment.getColumn(key);
            DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
            BitmapIndexedColumn indexedColumn = column.getBitmapIndex();
            RelationIndex relationIndex = foreignSegment.getRelation(RelationPathHelper.convert2CubeRelationPath(relationSource));
            final RelationColumn relationColumn = new RelationColumn(relationIndex, primarySegments, relationSource);
            for (String value : values) {
                final int index = dicColumn.getIndex(value);
                if (0 != index) {
                    ImmutableBitMap bitMap = indexedColumn.getBitMapIndex(index);
                    bitMap.traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {

                            RelationColumn.KeyRow keyRow = relationColumn.getPrimaryRows(row);
                            if (null != keyRow) {
                                for (int i = 0; i < size; i++) {
                                    if (filterSet[i] == null) {
                                        filterSet[i] = new HashSet<String>();
                                    }
                                    filterSet[i].add(keyRow.getData()[i].toString());
                                }
                            }
                        }
                    });
                }
            }
            relationColumn.release();
            relationIndex.release();
            indexedColumn.release();
            dicColumn.release();
        }
        for (int i = 0; i < size; i++) {
            filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(primaryFields.get(i), filterSet[i], SwiftDetailFilterType.STRING_IN));
        }
    }
}
