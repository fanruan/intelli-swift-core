package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.tools.BIMemDataSourceTestTool;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;

/**
 * This class created on 2016/4/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BISourceDataTransportTest extends BICubeTestBase {

    private BISourceDataTransport dataTransport;
    private BIMemDataSourceTestTool tableSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableSource = new BIMemDataSourceTestTool();
    }

    public void transport(ITableSource tableSource) {
        try {
            setUp();
            dataTransport = new BISourceDataTransport(cube, tableSource, new HashSet<ITableSource>());
            dataTransport.mainTask();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTransport() {
        try {
            transport(tableSource);
            DBField[] fields = tableSource.getFieldsArray(new HashSet<ITableSource>());
            ICubeColumnReaderService col1 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[0]));
            ICubeColumnReaderService col2 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[1]));
            ICubeColumnReaderService col3 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[2]));
            ICubeColumnReaderService col4 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[3]));
            ICubeColumnReaderService col5 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[4]));

            for (int i = 0; i < tableSource.getRowNumber(); i++) {
                assertEquals(col1.getOriginalValueByRow(i), tableSource.getDateData().get(i));
                assertEquals(col2.getOriginalValueByRow(i), tableSource.getStringData().get(i));
                assertEquals(col3.getOriginalValueByRow(i), tableSource.getDoubleData().get(i));
                assertEquals(col4.getOriginalValueByRow(i), tableSource.getLongData().get(i));
                assertEquals(col5.getOriginalValueByRow(i), tableSource.getDoubleData().get(i));
            }
            assertEquals(cube.getCubeTable(BITableKeyUtils.convert(tableSource)).getRowCount(), tableSource.getRowNumber());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
