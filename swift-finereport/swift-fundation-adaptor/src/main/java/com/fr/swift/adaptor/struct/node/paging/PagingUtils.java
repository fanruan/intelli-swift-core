package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.source.core.MD5Utils;

/**
 * Created by Lyon on 2018/5/21.
 */
public class PagingUtils {

    public static PagingInfo createPagingInfo(TableWidget widget, Expander expander) {
        boolean isFirstPage = isFirstPage(widget.getPage());
        boolean isNextPage = isNextPage(widget.getPage());
        int pageSize = getPageSize(widget);
        String pagingSessionId = getPagingSessionId(widget);
        return new PagingInfo(isFirstPage, isNextPage, pageSize, pagingSessionId, expander);
    }

    private static String getPagingSessionId(TableWidget widget) {
        String widgetId = widget.getWidgetId();
        String sessionId = widget.getValue().getSessionId();
        if (widgetId == null || sessionId == null) {
            return null;
        }
        return MD5Utils.getMD5String(new String[] { widgetId, sessionId });
    }

    public static int getPageSize(TableWidget widget) {
        if (widget.getPage() == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ALL_PAGE) {
            return Integer.MAX_VALUE;
        }
        return widget.getRowCounts();
    }

    public static boolean isRefresh(int pageOperation) {
        return pageOperation == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.REFRESH
                || pageOperation == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ALL_PAGE;
    }

    private static boolean isFirstPage(int pageOperation) {
        switch (pageOperation) {
            case BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ALL_PAGE:
            case BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.REFRESH:
                return true;
        }
        return false;
    }

    private static boolean isNextPage(int pageOperation) {
        switch (pageOperation) {
            case BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ROW_PRE:
            case BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.COLUMN_PRE:
                return false;
        }
        return true;
    }
}
