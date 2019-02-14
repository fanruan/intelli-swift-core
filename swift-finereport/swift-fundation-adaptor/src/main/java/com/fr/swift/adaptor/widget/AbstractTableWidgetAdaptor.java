package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.fr.swift.source.SourceKey;
import com.fr.swift.utils.BusinessTableUtils;

/**
 * @author pony
 * @date 2018/4/20
 */
public abstract class AbstractTableWidgetAdaptor extends AbstractWidgetAdaptor {
    protected static SourceKey getSourceKey(AbstractTableWidget widget) {
        String tableName = widget.getValue().getTableName();
        return new SourceKey(BusinessTableUtils.getSourceIdByTableId(tableName));
    }

}
