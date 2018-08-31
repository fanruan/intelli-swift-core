package com.fr.swift.source.excel;

import com.fr.swift.source.excel.exception.ExcelFileTypeException;
import com.fr.swift.source.excel.exception.ExcelTableHeaderException;
import com.fr.third.v2.org.apache.poi.ss.usermodel.Sheet;
import com.fr.third.v2.org.apache.poi.ss.usermodel.Workbook;
import com.fr.third.v2.org.apache.poi.ss.usermodel.WorkbookFactory;
import com.fr.third.v2.org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/21
 */
public class ExcelUtil {
    /**
     * 暂时这么处理，继承的ExcelDataModelPlus没法拿到mergeCell
     *
     * @param filePath
     */
    public static void checkHead(String filePath) {
        try {
            InputStream inputStream;
            if (isRemote(filePath)) {
                URL url = new URL(filePath);
                inputStream = url.openStream();
            } else {
                inputStream = new FileInputStream(filePath);
            }
            Workbook workBook = WorkbookFactory.create(inputStream);
            Sheet sheet = workBook.getSheetAt(0);
            if (sheet != null) {
                List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
                for (int i = 0; i < mergedRegions.size(); i++) {
                    CellRangeAddress range = sheet.getMergedRegion(i);
                    int firstRow = range.getFirstRow();
                    int lastRow = range.getLastRow();
                    if (0 == firstRow || 0 == lastRow) {
                        throw new ExcelTableHeaderException();
                    }
                }
            }
        } catch (ExcelTableHeaderException e) {
            throw e;
        } catch (Exception e) {
            throw new ExcelFileTypeException(e);
        }
    }

    public static boolean isRemote(String filePath) {
        return filePath.startsWith("http") || filePath.startsWith("HTTP");
    }
}
