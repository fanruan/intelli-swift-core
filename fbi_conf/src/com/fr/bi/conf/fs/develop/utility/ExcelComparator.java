package com.fr.bi.conf.fs.develop.utility;


import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by Connery on 2015/1/1.
 */
public class ExcelComparator {

    private static String generatorMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
        return value;
    }

    public boolean isExcelEqual(String path_excel_1, String path_excel_2) throws IOException {
        InputStream in_excel_1 = new FileInputStream(path_excel_1);
        InputStream in_excel_2 = new FileInputStream(path_excel_2);
        HSSFWorkbook workbook_1 = new HSSFWorkbook(in_excel_1);
        HSSFWorkbook workbook_2 = new HSSFWorkbook(in_excel_2);
        if (isExcelMD5Equal(path_excel_1, path_excel_2)) {
            return true;
        } else {
            return isExcelContentEqual(workbook_1, workbook_2);
        }
    }

    private boolean isExcelMD5Equal(String path_excel_1, String path_excel_2) {
        File excel_1_file = new File(path_excel_1);
        File excel_2_file = new File(path_excel_2);
        if (!excel_1_file.exists() || !excel_2_file.exists()) {
            return false;
        } else {
            String md5_excel_1 = generatorMd5ByFile(excel_1_file);
            String md5_excel_2 = generatorMd5ByFile(excel_2_file);
            return ComparatorUtils.equals(md5_excel_1, md5_excel_2);

        }


    }

    private boolean isExcelContentEqual(HSSFWorkbook excel_1, HSSFWorkbook excel_2) {
        if (isExcelStructureEqual(excel_1, excel_2)) {
            for (int numSheet = 0; numSheet < excel_1.getNumberOfSheets(); numSheet++) {
                HSSFSheet excel_1_sheet = excel_1.getSheetAt(numSheet);
                HSSFSheet excel_2_sheet = excel_2.getSheetAt(numSheet);
                for (int rowNum = 1; rowNum <= excel_1_sheet.getLastRowNum(); rowNum++) {
                    HSSFRow excel_1_row = excel_1_sheet.getRow(rowNum);
                    HSSFRow excel_2_row = excel_2_sheet.getRow(rowNum);
                    for (int columnNum = 1; columnNum < excel_1_row.getLastCellNum(); columnNum++) {
                        if (!ComparatorUtils.equals(getValue(excel_1_row.getCell(columnNum)), getValue(excel_2_row.getCell(columnNum)))) {
                            return false;
                        } else {
                            continue;
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean isExcelStructureEqual(HSSFWorkbook excel_1, HSSFWorkbook excel_2) {
        if (excel_1.getNumberOfSheets() != excel_2.getNumberOfSheets()) {
            return false;
        } else {
            for (int numSheet = 0; numSheet < excel_1.getNumberOfSheets(); numSheet++) {
                HSSFSheet excel_1_sheet = excel_1.getSheetAt(numSheet);
                HSSFSheet excel_2_sheet = excel_2.getSheetAt(numSheet);
                if (excel_1_sheet.getLastRowNum() != excel_2_sheet.getLastRowNum()) {
                    return false;
                } else {
                    for (int rowNum = 1; rowNum <= excel_1_sheet.getLastRowNum(); rowNum++) {
                        HSSFRow excel_1_row = excel_1_sheet.getRow(rowNum);
                        HSSFRow excel_2_row = excel_2_sheet.getRow(rowNum);
                        if (excel_1_row.getLastCellNum() != excel_2_row.getLastCellNum()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}