package com.fr.bi.web.conf.utils;

import com.fr.base.ExcelUtils;
import com.fr.base.FRContext;
import com.fr.base.Parameter;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cal.report.report.BIHtmlWriter;
import com.fr.bi.cal.report.report.cell.BICellWriter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.io.importer.Excel2007ReportImporter;
import com.fr.io.importer.ExcelReportImporter;
import com.fr.main.impl.WorkBook;
import com.fr.main.workbook.AnalyWorkBook;
import com.fr.report.core.ReportUtils;
import com.fr.report.worksheet.AnalysisRWorkSheet;
import com.fr.stable.Constants;
import com.fr.stable.ViewActor;
import com.fr.stable.html.Tag;
import com.fr.stable.web.Repository;
import com.fr.web.core.ReportRepositoryDeal;
import com.fr.web.core.SessionIDInfor;
import com.fr.web.output.html.chwriter.CellHtmlWriter;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Created by zcf on 2016/12/5.
 */
public class BIGetImportedExcelHTML {
    public static SessionIDInfor defalutSession = new BISession("0", new BIWeblet(), -999);
    private String fullFileName;
    private File excelFile;
    private static String DATA_PATH = FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH;

    public File getFile() {
        return excelFile;
    }
    public BIGetImportedExcelHTML(String fullFileName){
        this.fullFileName = fullFileName;
        File parentFile = new File(DATA_PATH);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        excelFile = new File(parentFile, fullFileName);
    }

    public Tag getHtmlFromExcel(HttpServletRequest req) {
        Tag t = new Tag("div");

        try {
            Repository repo = new ReportRepositoryDeal(req, defalutSession, Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION);
            FileInputStream in = new FileInputStream(excelFile);
            WorkBook workbook = null;
            if(ExcelUtils.checkPOIJarExist() && excelFile.getAbsolutePath().endsWith(".xlsx")){
                workbook = new Excel2007ReportImporter().generateWorkBookByStream(in);
            } else {
                workbook = new ExcelReportImporter().generateWorkBookByStream(in);
            }
            AnalyWorkBook ar = (AnalyWorkBook) workbook.execute(new HashMap<String, Parameter>(), new ViewActor());

            AnalysisRWorkSheet rsReport = (AnalysisRWorkSheet)ar.getReport(0);
            CellHtmlWriter cellWriter = new BICellWriter(repo , 0, ReportUtils.getReportSettings(rsReport));

            rsReport.writeHtml(t, BIHtmlWriter.getInstance(), cellWriter, 0, repo);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }

        return t;
    }
}
