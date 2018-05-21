package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.fr.swift.query.adapter.dimension.Expander;

/**
 * Created by Lyon on 2018/5/21.
 */
public class PagingUtils {

    public static PagingInfo createPagingInfo(TableWidget widget, Expander expander) {
        boolean isFirstPage = isFirstPage(widget.getPage());
        boolean isNextPage = isNextPage(widget.getPage());
        int pageSize = getPageSize(widget);
        return new PagingInfo(isFirstPage, isNextPage, pageSize, widget.getValue().getSessionId(), expander);
    }

    public static int getPageSize(TableWidget widget) {
        if (widget.getPage() == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ALL_PAGE) {
            return Integer.MAX_VALUE;
        }
        return widget.getRowCounts();
    }

    public static boolean isRefresh(int pageOperation) {
        return pageOperation == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.REFRESH;
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
