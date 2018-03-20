package com.fr.swift.generate.excel;

import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelDataSource;
import junit.framework.TestCase;

/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class BaseExcelTest extends TestCase {

    protected String path1 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
    protected String path2 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test1.xlsx");
    protected String path3 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test2.xlsx");

    protected ExcelDataSource dataSource;
    protected String[] names = {"A", "B"};
    protected ColumnTypeConstants.ColumnType[] types = {ColumnTypeConstants.ColumnType.STRING, ColumnTypeConstants.ColumnType.NUMBER};

}
