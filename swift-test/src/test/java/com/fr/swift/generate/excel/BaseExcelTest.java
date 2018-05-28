package com.fr.swift.generate.excel;

import com.fr.swift.generate.BaseConfigTest;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.test.Preparer;

/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class BaseExcelTest extends BaseConfigTest {

    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseExcelTest.class);

    protected String path1 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
    protected String path2 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test1.xlsx");
    protected String path3 = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test2.xlsx");

    protected ExcelDataSource dataSource;
    protected String[] names = {"A", "B"};
    protected ColumnTypeConstants.ColumnType[] types = {ColumnTypeConstants.ColumnType.STRING, ColumnTypeConstants.ColumnType.NUMBER};

    @Override
    protected void setUp() throws Exception {
        Preparer.prepareCubeBuild();
    }
}
