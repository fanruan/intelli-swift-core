package com.fr.bi.cal.analyze.executor.paging;

import com.fr.bi.cal.analyze.cal.result.operator.AllPageOperator;
import com.fr.bi.cal.analyze.cal.result.operator.BigDataChartOperator;
import com.fr.bi.cal.analyze.cal.result.operator.LastPageOperator;
import com.fr.bi.cal.analyze.cal.result.operator.NextPageOperator;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.cal.analyze.cal.result.operator.RefreshPageOperator;
import com.fr.bi.cal.analyze.report.report.widget.SummaryWidget;
import com.fr.bi.stable.constant.BIExcutorConstant.PAGINGTYPE;
import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by GUY on 2015/4/17.
 */
public class PagingFactory {
    public static final int PAGE_PER_GROUP_20 = 20;

    public static final int PAGE_PER_GROUP_5 = 5;

    public static final int PAGE_PER_GROUP_100 = 100;

    public static Paging createPaging(int type) {
        return createPaging(type, 0);
    }

    public static Paging createPaging(int type, int operator) {
        Paging paging = new Paging();
        paging.setOperator(operator);
        switch (type) {
            case PAGINGTYPE.NONE:
                paging.setPageSize(Integer.MAX_VALUE);
                break;
            case PAGINGTYPE.GROUP20:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
            case PAGINGTYPE.GROUP100:
                paging.setPageSize(PAGE_PER_GROUP_100);
                break;
            case PAGINGTYPE.GROUP5:
                paging.setPageSize(PAGE_PER_GROUP_5);
                break;
            default:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
        }
        return paging;
    }

    public static Operator createRowOperator(int type, SummaryWidget widget) {

        Operator operator;
        switch (type) {
            case BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE:
                operator = new AllPageOperator();
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH:
                operator = new NextPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.ROW_NEXT:
                operator = new NextPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.ROW_PRE:
                operator = new LastPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.EXPAND:
                operator = new RefreshPageOperator(widget.getClickValue(), widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.BIGDATACHART:
                operator = new BigDataChartOperator();
                break;
            default:
                operator = new RefreshPageOperator(widget.getMaxRow());
                break;
        }
        return operator;
    }

    public static Operator createColumnOperator(int type, SummaryWidget widget) {
        //pony 横向的改成全部展示
        Operator operator;
        switch (type) {
            case BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE:
                operator = new AllPageOperator();
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH:
                operator = new NextPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.COLUMN_NEXT:
                operator = new NextPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.COLUMN_PRE:
                operator = new LastPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.EXPAND:
                operator = new RefreshPageOperator(widget.getClickValue(), widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.BIGDATACHART:
                operator = new BigDataChartOperator();
                break;
            default:
                operator = new RefreshPageOperator(widget.getMaxCol());
                break;
        }
        return operator;
    }

}