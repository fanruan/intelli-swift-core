package com.fr.swift.generate.excel;

import com.fr.swift.generate.BaseConfigTest;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.test.Preparer;
import com.fr.workspace.simple.SimpleWork;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class BaseExcelTest extends BaseConfigTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SimpleWork.checkIn(ResourceUtils.class.getClassLoader().getResource("").getPath());
    }

    @After
    public void tearDown() throws Exception {
        SimpleWork.checkOut();
    }

    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseExcelTest.class);

    protected String path1 = "com/fr/swift/resource/excel/test.xlsx";
    protected String path2 = "com/fr/swift/resource/excel/test1.xlsx";
    protected String path3 = "com/fr/swift/resource/excel/test2.xlsx";

    protected ExcelDataSource dataSource;
    protected String[] names = {"A", "B"};
    protected ColumnTypeConstants.ColumnType[] types = {ColumnTypeConstants.ColumnType.STRING, ColumnTypeConstants.ColumnType.NUMBER};

    @BeforeClass
    public static void boot() throws Exception {
        Preparer.prepareCubeBuild();
    }
}
