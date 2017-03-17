package com.fr.bi.cal.report.io;

import com.fr.stable.ExportConstants;


/**
 * Created by daniel on 2016/7/13.
 */
public class BIExportUtils {

    public static int createExcel2007Page (int row) {
        return (row / ExportConstants.MAX_ROWS_2007) + 1;
    }
}
