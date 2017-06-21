package com.fr.bi.cal.analyze.report.report.export;

import com.fr.bi.cal.analyze.cal.result.ComplexAllExpander;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.report.report.export.utils.BIReportExportExcelUtils;
import com.fr.bi.cal.analyze.report.report.widget.*;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.function.FLOOR;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 全局导出excel
 * Created by AstronautOO7 on 2017/1/11.
 */
public class BIReportExportExcel {

    private BISession session;
    protected BIReport report = new BIReportor();

    public BIReportExportExcel(String sessionID) throws Exception {
        this.session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
    }

    public ResultWorkBook getExcelExportBook(HttpServletRequest req) throws Exception {
        BIConvertWidgetsToFL biConvertWidgetsToFL = new BIConvertWidgetsToFL(session, req);

        if (biConvertWidgetsToFL.isReportEmpty()) {
            return null;
        }

        BIWorkBook wb = new BIWorkBook();
        BIPolyWorkSheet reportSheet = new BIPolyWorkSheet();
        PolyECBlock polyECBlock = BIReportExportExcelUtils.createPolyECBlock("Dashboard");
        polyECBlock.getBlockAttr().setFreezeHeight(true);
        polyECBlock.getBlockAttr().setFreezeWidth(true);
        ArrayList<FloatElement> floatElements = biConvertWidgetsToFL.getFloatElements();

        if (floatElements.size() != 0) {
            for (FloatElement fl : floatElements) {
                polyECBlock.addFloatElement(fl);
            }
        }

        reportSheet.addBlock(polyECBlock);
        wb.addReport("Dashboard", reportSheet);
        createOtherSheets(biConvertWidgetsToFL, wb);

        return wb.execute4BI(session.getParameterMap4Execute());
    }

    private void createOtherSheets(BIConvertWidgetsToFL biConvertWidgetsToFL, BIWorkBook wb) throws CloneNotSupportedException {
        ArrayList<BIWidget> widgets = biConvertWidgetsToFL.getCommonWidgets();
        //other sheets
        if (widgets.size() == 0) {
            return;
        }
        for (BIWidget widget : widgets) {
            if (BIReportExportExcelUtils.widgetHasData(widget)) {
                widget = (BIWidget) widget.clone();
                switch (widget.getType()) {
                    case TABLE:
                    case CROSS_TABLE:
                    case COMPLEX_TABLE:
                        ((TableWidget) widget).setComplexExpander(new ComplexAllExpander());
                        ((TableWidget) widget).setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                        break;
                    case DETAIL:
                        ((BIDetailWidget) widget).setPage(BIExcutorConstant.PAGINGTYPE.NONE);
                        break;
                }

                BIPolyWorkSheet ws = widget.createWorkSheet(session);
                wb.addReport(widget.getWidgetName(), ws);
            } else {
                BIPolyWorkSheet emptySheet = new BIPolyWorkSheet();
                emptySheet.addBlock(BIReportExportExcelUtils.createPolyECBlock(widget.getWidgetName()));
                wb.addReport(widget.getWidgetName(), emptySheet);
            }
        }
    }
}
