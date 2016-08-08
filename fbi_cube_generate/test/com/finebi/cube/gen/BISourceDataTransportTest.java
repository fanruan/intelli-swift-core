package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.BIDiskWriterReaderTest;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.gen.oper.BISourceDataAllTransport;
import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.finebi.cube.gen.subset.BISourceDataNeverTransport4Test;
import com.finebi.cube.gen.subset.BISourceDataPartTransport4Test;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.structure.table.CompoundCubeTableReader;
import com.finebi.cube.tools.*;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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
            dataTransport = new BISourceDataAllTransport(cube, tableSource, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>(), 1);
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
            CubeColumnReaderService col1 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[0]));
            CubeColumnReaderService col2 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[1]));
            CubeColumnReaderService col3 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[2]));
            CubeColumnReaderService col4 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[3]));
            CubeColumnReaderService col5 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[4]));

            for (int i = 0; i < tableSource.getRowNumber(); i++) {
                assertEquals(col1.getOriginalObjectValueByRow(i), tableSource.getDateData().get(i));
                assertEquals(col2.getOriginalObjectValueByRow(i), tableSource.getStringData().get(i));
                assertEquals(col3.getOriginalObjectValueByRow(i), tableSource.getFloatData().get(i));
                assertEquals(col4.getOriginalObjectValueByRow(i), tableSource.getLongData().get(i));
                assertEquals(col5.getOriginalObjectValueByRow(i), tableSource.getDoubleData().get(i));
            }
            assertEquals(cube.getCubeTable(BITableKeyUtils.convert(tableSource)).getRowCount(), tableSource.getRowNumber());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /*增量更新：
* 先插入：{id：1，name：China;ID:2,name：US}
* 再加入:{id:3,name:Japan,id:4,name:Canada，id：5，name：Mexio}*/
    public void testPartUpdate() throws BICubeColumnAbsentException {

        CubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(BINationDataFactory.createTableNationByPart()));
        transport(BINationDataFactory.createTableNation());
        assertTrue(tableEntityService.getColumnDataGetter("name").getOriginalObjectValueByRow(0).equals("China"));
        assertTrue(tableEntityService.getColumnDataGetter("name").getOriginalObjectValueByRow(1).equals("US"));
        assertTrue(tableEntityService.getRowCount() == 2);
        transportByPart(BINationDataFactory.createTableNationByPart(), 2);

//        for (ICubeFieldSource fieldSource : BINationDataFactory.createTableNation().getSelfFields(null)) {
//            CubeColumnReaderService column = cube.getCubeColumn(BITableKeyUtils.convert(BINationDataFactory.createTableNation()), BIColumnKey.covertColumnKey(fieldSource));
//            if (fieldSource.getFieldName().equals("name")) {
//                column.getOriginalObjectValueByRow(0).equals("China");
//
//            }
//        }
        assertTrue(tableEntityService.getRowCount() == 5);
        assertTrue(tableEntityService.getColumnDataGetter("name").getOriginalObjectValueByRow(0).equals("China"));
        assertTrue(tableEntityService.getColumnDataGetter("name").getOriginalObjectValueByRow(1).equals("US"));
    }

    /**
     * 首次全量更新，之后不更新
     * @throws BICubeColumnAbsentException
     */
    public void testNeverTransportTable() throws BICubeColumnAbsentException, URISyntaxException {
        transport(tableSource);
        ICubeConfiguration tempConf = new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                return URI.create(BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "temptable").getAbsolutePath());
            }
        };
        BICubeLocation to = new BICubeLocation(tempConf.getRootURI().getPath().toString(), tableSource.getSourceID());
        if (!new File(to.getAbsolutePath()).exists()){
            new File(to.getAbsolutePath()).mkdirs();
        }
        dataTransport = new BISourceDataNeverTransport4Test(cube, tableSource, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>());
        dataTransport.mainTask(null);

        retrievalService = new BICubeResourceRetrieval(tempConf);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        tableEntity = (BICubeTableEntity) cube.getCubeTableWriter(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
        BICubeFieldSource[] fields = tableSource.getFieldsArray(new HashSet<CubeTableSource>());
        CubeColumnReaderService col1 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[0]));
        CubeColumnReaderService col2 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[1]));
        CubeColumnReaderService col3 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[2]));
        CubeColumnReaderService col4 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[3]));
        CubeColumnReaderService col5 = cube.getCubeColumn(BITableKeyUtils.convert(tableSource), BIColumnKey.covertColumnKey(fields[4]));

        for (int i = 0; i < tableSource.getRowNumber(); i++) {
            assertEquals(col1.getOriginalObjectValueByRow(i), tableSource.getDateData().get(i));
            assertEquals(col2.getOriginalObjectValueByRow(i), tableSource.getStringData().get(i));
            assertEquals(col3.getOriginalObjectValueByRow(i), tableSource.getFloatData().get(i));
            assertEquals(col4.getOriginalObjectValueByRow(i), tableSource.getLongData().get(i));
            assertEquals(col5.getOriginalObjectValueByRow(i), tableSource.getDoubleData().get(i));
        }
        assertEquals(cube.getCubeTable(BITableKeyUtils.convert(tableSource)).getRowCount(), tableSource.getRowNumber());

    }

    public void testTransportCompoundTable() {
        try {
            BIMemDataSourceDependent tableSource = new BIMemDataSourceDependent();
            Set<CubeTableSource> parents = new HashSet<CubeTableSource>();
            new BISourceDataAllTransport(cube, tableSource.parent, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>(), 1).mainTask(null);
            parents.add(tableSource.parent);
            dataTransport = new BISourceDataAllTransport(cube, tableSource, new HashSet<CubeTableSource>(), parents, 1);
            dataTransport.mainTask(null);


            CompoundCubeTableReader compoundTable = (CompoundCubeTableReader) cube.getCubeTable(BITableKeyUtils.convert(tableSource));
            int size = tableSource.getFieldsArray(null).length + tableSource.parent.getFacetFields(null).size();
            assertEquals(size, compoundTable.getFieldInfo().size());
            CubeTableEntityGetterService parent = compoundTable.getParentTable();
            BIMemoryDataSource memoryDataSource = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableA();
            ICubeFieldSource[] fields = tableSource.parent.getFieldsArray(new HashSet<CubeTableSource>());
            CubeColumnReaderService col1 = compoundTable.getColumnDataGetter(BIColumnKey.covertColumnKey(fields[0]));
            CubeColumnReaderService col2 = compoundTable.getColumnDataGetter(BIColumnKey.covertColumnKey(fields[1]));
            CubeColumnReaderService col3 = compoundTable.getColumnDataGetter(BIColumnKey.covertColumnKey(fields[2]));
            CubeColumnReaderService col4 = compoundTable.getColumnDataGetter(BIColumnKey.covertColumnKey(fields[3]));

            for (int i = 0; i < memoryDataSource.rowCount; i++) {
                assertEquals(col1.getOriginalObjectValueByRow(i), memoryDataSource.contents.get(0).get(i));
                assertEquals(col2.getOriginalObjectValueByRow(i), memoryDataSource.contents.get(1).get(i));
                assertEquals(col3.getOriginalObjectValueByRow(i), memoryDataSource.contents.get(2).get(i));
                assertEquals(col4.getOriginalObjectValueByRow(i), memoryDataSource.contents.get(3).get(i));
            }

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * Detail: ��compoundTable�Ĺ��������ӱ���ֶ���ȡ����
     * Author:Connery
     * Date:2016/6/20
     */
    public void testCompoundSubTableRelation() {
        try {

            BIMemDataSourceDependent tableSource = new BIMemDataSourceDependent();
            Set<CubeTableSource> parents = new HashSet<CubeTableSource>();
            new BISourceDataAllTransport(cube, tableSource.parent, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>(), 1).mainTask(null);
            parents.add(tableSource.parent);
            dataTransport = new BISourceDataAllTransport(cube, tableSource, new HashSet<CubeTableSource>(), parents, 1);
            dataTransport.mainTask(null);


            CompoundCubeTableReader compoundTable = (CompoundCubeTableReader) cube.getCubeTable(BITableKeyUtils.convert(tableSource));

            BIMemoryDataSource B = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableB();
            BITableSourceRelation relation = new BITableSourceRelation(DBFieldTestTool.generateSTRING(),
                    B.fieldList.get(2), tableSource, B);
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(relation);
            BIMemoryDataSource parent = (BIMemoryDataSource) tableSource.parent;

            CubeColumnReaderService columnReaderService = compoundTable.getColumnDataGetter(BIColumnKey.covertColumnKey(parent.fieldList.get(0)));
            columnReaderService.getRelationIndexGetter(BICubePathUtils.convert(path));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void transportByPart(CubeTableSource tableSource, int oldCount) {
        try {
            dataTransport = new BISourceDataPartTransport4Test(cube, tableSource, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>(), oldCount);
            dataTransport.mainTask(null);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
