package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.finebi.cube.structure.ICubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.tools.BIMemDataSourceTestToolCube;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/4/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BISourceDataTransportTest extends BICubeTestBase {

    private BISourceDataTransport dataTransport;
    private BIMemDataSourceTestToolCube tableSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableSource = new BIMemDataSourceTestToolCube();
    }

    public void transport(CubeTableSource tableSource) {
        try {
            setUp();
            dataTransport = new BISourceDataTransport(cube, tableSource, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>());
            dataTransport.mainTask(null);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTransport() {
        try {
            transport(tableSource);
            BICubeFieldSource[] fields = tableSource.getFieldsArray(new HashSet<CubeTableSource>());
            ICubeColumnReaderService col1 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[0]));
            ICubeColumnReaderService col2 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[1]));
            ICubeColumnReaderService col3 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[2]));
            ICubeColumnReaderService col4 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[3]));
            ICubeColumnReaderService col5 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[4]));

            for (int i = 0; i < tableSource.getRowNumber(); i++) {
                assertEquals(col1.getOriginalValueByRow(i), tableSource.getDateData().get(i));
                assertEquals(col2.getOriginalValueByRow(i), tableSource.getStringData().get(i));
                assertEquals(col3.getOriginalValueByRow(i), tableSource.getFloatData().get(i));
                assertEquals(col4.getOriginalValueByRow(i), tableSource.getLongData().get(i));
                assertEquals(col5.getOriginalValueByRow(i), tableSource.getDoubleData().get(i));
            }
            assertEquals(cube.getCubeTable(BITableKeyUtils.convert(tableSource)).getRowCount(), tableSource.getRowNumber());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testTransportCompoundTable() {
        try {
            Set<CubeTableSource> parents = new HashSet<CubeTableSource>();
            new BISourceDataTransport(cube, BIMemoryDataSourceFactory.generateTableA(), new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>()).mainTask(null);
            parents.add(BIMemoryDataSourceFactory.generateTableA());
            dataTransport = new BISourceDataTransport(cube, tableSource, new HashSet<CubeTableSource>(), parents);
            dataTransport.mainTask(null);


            ICubeTableEntityGetterService compoundTable = cube.getCubeTable(BITableKeyUtils.convert(tableSource));
            int size = tableSource.getFieldsArray(null).length + BIMemoryDataSourceFactory.generateTableA().getFieldsArray(null).length;
            assertEquals(size, compoundTable.getFieldInfo().size());

            BIMemoryDataSource memoryDataSource = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableA();
            ICubeFieldSource[] fields = BIMemoryDataSourceFactory.generateTableA().getFieldsArray(new HashSet<CubeTableSource>());
            ICubeColumnReaderService col1 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[0]));
            ICubeColumnReaderService col2 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[1]));
            ICubeColumnReaderService col3 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[2]));
            ICubeColumnReaderService col4 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[3]));

            for (int i = 0; i < memoryDataSource.rowCount; i++) {
                assertEquals(col1.getOriginalValueByRow(i), memoryDataSource.contents.get(0).get(i));
                assertEquals(col2.getOriginalValueByRow(i), memoryDataSource.contents.get(1).get(i));
                assertEquals(col3.getOriginalValueByRow(i), memoryDataSource.contents.get(2).get(i));
                assertEquals(col4.getOriginalValueByRow(i), memoryDataSource.contents.get(3).get(i));
            }

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
