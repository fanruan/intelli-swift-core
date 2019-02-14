package com.fr.swift.source.excel;

import com.fr.general.data.TableDataException;
import com.fr.swift.source.excel.data.CSVDataModel;
import com.fr.swift.source.excel.data.ExcelDataModel;
import com.fr.swift.source.excel.data.ExcelDataModelCreator;
import com.fr.swift.source.excel.data.IExcelDataModel;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author yee
 * @date 2018/4/26
 */
public class ExcelDataModelTest extends TestCase {


    // 2003最大就只有65535行
    public void test2003() throws TableDataException {
        excelPathTest(ExcelInfo.getUrl2003());
    }

    public void test2007() throws TableDataException {
        excelPathTest(ExcelInfo.getUrl2007());
    }

    public void testCsv() throws TableDataException {
        IExcelDataModel dataModel = ExcelDataModelCreator.createDataModel(ExcelInfo.getUrlcsv());
        assertTrue(dataModel instanceof CSVDataModel);
        String[] names = dataModel.onlyGetColumnNames();
        assertEquals(String.join(",", ExcelInfo.getColumnNames()), String.join(",", names));
        int rowCount = 0;
        while (dataModel.hasRow(rowCount++)) ;
        assertEquals(dataModel.getRowCount(), 10000);
    }

    public void test2003Stream() throws IOException, TableDataException {
        excelStreamTest(ExcelInfo.getUrl2003());
    }

    public void test2007Stream() throws IOException, TableDataException {
        excelStreamTest(ExcelInfo.getUrl2007());
    }

    public void testCsvStream() throws TableDataException, IOException {
        InputStream inputStream = new FileInputStream(ExcelInfo.getUrlcsv());
        IExcelDataModel dataModel = ExcelDataModelCreator.createDataModel(inputStream, ExcelInfo.getUrlcsv());
        assertTrue(dataModel instanceof CSVDataModel);
        String[] names = dataModel.onlyGetColumnNames();
        assertEquals(String.join(",", ExcelInfo.getColumnNames()), String.join(",", names));
        int rowCount = 0;
        while (dataModel.hasRow(rowCount++)) ;
        assertEquals(dataModel.getRowCount(), 10000);
    }

    private void excelPathTest(String path) throws TableDataException {
        long start = System.currentTimeMillis();
        IExcelDataModel dataModel = ExcelDataModelCreator.createDataModel(path);
        assertTrue(dataModel instanceof com.fr.swift.source.excel.data.ExcelDataModel);
        String[] names = dataModel.onlyGetColumnNames();
        System.out.println(System.currentTimeMillis() - start);
        assertEquals(dataModel.getRowCount(), 10000);
        assertEquals(String.join(",", names), String.join(",", ExcelInfo.getColumnNames()));
    }

    private void excelStreamTest(String path) throws TableDataException, IOException {
        long start = System.currentTimeMillis();
        InputStream inputStream = new FileInputStream(path);
        IExcelDataModel dataModel = ExcelDataModelCreator.createDataModel(inputStream, path);
        assertTrue(dataModel instanceof ExcelDataModel);
        String[] names = dataModel.onlyGetColumnNames();
        System.out.println(System.currentTimeMillis() - start);
        assertEquals(dataModel.getRowCount(), 10000);
        assertEquals(String.join(",", names), String.join(",", ExcelInfo.getColumnNames()));
    }
}