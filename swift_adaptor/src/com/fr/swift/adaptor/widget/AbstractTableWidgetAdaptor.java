package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.stable.StringUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

/**
 * @author pony
 * @date 2018/4/20
 */
public abstract class AbstractTableWidgetAdaptor extends AbstractWidgetAdaptor {
    protected static SourceKey getSourceKey(AbstractTableWidget widget) throws Exception {
        String fieldId = null;
        if (!widget.getDimensionList().isEmpty()) {
            fieldId = getFieldId(widget.getDimensionList().get(0));
        } else if (!widget.getTargetList().isEmpty()) {
            fieldId = widget.getTargetList().get(0).getFieldId();
        }
        if (StringUtils.isEmpty(fieldId)) {
            return Crasher.crash("empty widget");
        }
        return new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
    }

}
