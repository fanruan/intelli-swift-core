package com.fr.swift.adaptor.widget.jump;

import com.finebi.conf.constant.BIDesignConstants.DESIGN;
import com.finebi.conf.constant.BIDesignConstants.DESIGN.WIDGET;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.BusinessTableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/5/18
 */
public class JumpAdaptor {
    public static Map<String, Object> getClickValue(FineWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        switch (widget.getType()) {
            case WIDGET.DETAIL:
                return getClickDetailValue((DetailWidget) widget, clicked, fieldIds);
            case WIDGET.TABLE:
                return getClickGroupValue((TableWidget) widget, clicked, fieldIds);
            default:
                return Collections.emptyMap();
        }
    }

    private static Map<String, Object> getClickGroupValue(TableWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        List<Map> clickMaps = (List<Map>) clicked.get("value");
        // fieldId -> value
        Map<String, String> clicks = new HashMap<String, String>();
        TableWidgetBean widgetBean = widget.getValue();
        for (Map clickMap : clickMaps) {
            String dId = clickMap.get("dId").toString();
            clicks.put(widgetBean.getDimensions().get(dId).getFieldId(), clickMap.get("text").toString());
        }
        List<WidgetBeanField> widgetBeanFields = widgetBean.getFields();
        List<Dimension> dims = new ArrayList<Dimension>();
        SourceKey sourceKey = null;
        for (int i = 0; i < fieldIds.size(); i++) {
            String fieldId = fieldIds.get(i);
            if (sourceKey == null) {
                sourceKey = BusinessTableUtils.getSourceKey(fieldId);
            }
            ColumnKey columnKey = BusinessTableUtils.getColumnKey(fieldId);
            for (WidgetBeanField widgetBeanField : widgetBeanFields) {
                if (ComparatorUtils.equals(fieldId, widgetBeanField.getId())) {
                    FilterInfo filterInfo = null;
                    if (clicks.containsKey(fieldId)) {
                        filterInfo = LinkageAdaptor.dealFilterInfo(columnKey, clicks.get(fieldId), widgetBeanField.getType());
                    }
                    dims.add(new DetailDimension(i, sourceKey, columnKey, null, null, filterInfo));
                    break;
                }
            }
        }
//        QueryInfo queryInfo = new DetailQueryInfo(new AllCursor(), widget.getWidgetId(), dims.toArray(new Dimension[0]), sourceKey, null, null, null, null);
        QueryInfo queryInfo = null;
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        Map<String, Object> pairs = new HashMap<String, Object>();
        if (resultSet.next()) {
            Row row = resultSet.getRowData();
            for (int i = 0; i < fieldIds.size(); i++) {
                String fieldId = fieldIds.get(i);
                pairs.put(fieldId, row.getValue(i));
            }
        }
        return pairs;
    }

    private static Map<String, Object> getClickDetailValue(DetailWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        int pageCount = Integer.valueOf(clicked.get("pageCount").toString());
        int rowIndex = Integer.valueOf(clicked.get("rowIndex").toString());
        int rowCount = (pageCount - 1) * DESIGN.DEFAULT_PAGE_ROW_SIZE + rowIndex;
        Dimension[] dims = new DetailDimension[fieldIds.size()];
        int i = 0;
        SourceKey sourceKey = null;
        for (String fieldId : fieldIds) {
            if (sourceKey == null) {
                sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
            }
            dims[i] = new DetailDimension(i, sourceKey, new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)), null, null, null);
            i++;
        }
//        QueryInfo queryInfo = new DetailQueryInfo(new AllCursor(), widget.getWidgetId(), dims, sourceKey, null, null, null, null);
        QueryInfo queryInfo = null;
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        int cursor = 0;
        Map<String, Object> pairs = new HashMap<String, Object>();
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            if (cursor++ < rowCount) {
                continue;
            }
            for (int j = 0; j < fieldIds.size(); j++) {
                pairs.put(fieldIds.get(j), row.getValue(j));
            }
            return pairs;
        }
        return pairs;
    }
}