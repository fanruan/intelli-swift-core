package com.fr.bi.cal.report.io;

import com.fr.bi.cal.report.io.core.BIExcelExporterBlock;
import com.fr.report.block.Block;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.ResultChartBlock;
import com.fr.stable.ExportConstants;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.awt.*;

/**
 * Created by daniel on 2016/7/13.
 */
public class BIExportUtils {

    public static int createExcel2007Page (int row) {
        return (row / ExportConstants.MAX_ROWS_2007) + 1;
    }
}
