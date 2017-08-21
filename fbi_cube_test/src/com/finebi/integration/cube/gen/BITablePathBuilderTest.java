package com.finebi.integration.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIBasicBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.gen.oper.BITablePathIndexBuilder;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.tools.BICubePathTestTool;
import com.finebi.cube.tools.BICubeRelationTestTool;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.stable.bridge.StableFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BITablePathBuilderTest extends BICubeTestBase {
    private BITablePathIndexBuilder tablePathIndexBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new BITablePathBuilderTestPackageConfigurationManager());
    }

    public void buildTablePath() {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tablePathIndexBuilder = new BITablePathIndexBuilder(cube, null, BICubePathTestTool.getABC());
        BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
        transportTest.transport(BIMemoryDataSourceFactory.generateTableA());
        transportTest.transport(BIMemoryDataSourceFactory.generateTableB());
        transportTest.transport(BIMemoryDataSourceFactory.generateTableC());

//        BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
//        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableA(), 1);
//        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableC(), 2);
//        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableB(), 1);
//        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableB(), 2);

        BIFieldIndexGeneratorTest.buildFieldIndex((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableA());
        BIFieldIndexGeneratorTest.buildFieldIndex((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableB());
        BIFieldIndexGeneratorTest.buildFieldIndex((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableC());

        BIRelationIndexBuilderTest relationIndexBuilder = new BIRelationIndexBuilderTest();
        relationIndexBuilder.setTableA(BIMemoryDataSourceFactory.generateTableA());
        relationIndexBuilder.setTableB(BIMemoryDataSourceFactory.generateTableB());
        relationIndexBuilder.generateRelationIndex(BICubeRelationTestTool.getTaTb(),
                BIMemoryDataSourceFactory.generateTableA(),
                BIMemoryDataSourceFactory.generateTableB(), 1, 2);

        relationIndexBuilder.setTableA(BIMemoryDataSourceFactory.generateTableB());
        relationIndexBuilder.setTableB(BIMemoryDataSourceFactory.generateTableC());
        relationIndexBuilder.generateRelationIndex(BICubeRelationTestTool.getTbTc(), BIMemoryDataSourceFactory.generateTableB(),
                BIMemoryDataSourceFactory.generateTableC(), 1, 2);
        tablePathIndexBuilder.mainTask(null);
    }

    // TODO: 2017-2-16 need repaired 
    public void testPathIndex() {
        try {
            buildTablePath();
            CubeRelationEntityGetterService getterService = cube.getCubeRelation(BITableKeyUtils.convert(BIMemoryDataSourceFactory.generateTableA()), BICubePathTestTool.getABC());
            assertEquals(getterService.getBitmapIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new int[]{1, 7}));
            assertEquals(getterService.getBitmapIndex(1), RoaringGroupValueIndex.createGroupValueIndex(new int[]{4, 6}));
            assertEquals(getterService.getBitmapIndex(2), RoaringGroupValueIndex.createGroupValueIndex(new int[]{}));
            assertEquals(getterService.getBitmapIndex(3), RoaringGroupValueIndex.createGroupValueIndex(new int[]{}));
            assertEquals(getterService.getBitmapIndex(4), RoaringGroupValueIndex.createGroupValueIndex(new int[]{}));
            assertEquals(getterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new int[]{0, 2, 3, 5}));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    // TODO: 2017-2-16 need repaired
    public void testEmptyPath() {
        try {
            buildEmptyTablePath();
//            CubeRelationEntityGetterService getterService = cube.getCubeRelation(BITableKeyUtils.convert(BIMemoryDataSourceFactory.generateTableA()), BICubePathTestTool.getABC());
//            assertEquals(getterService.getBitmapIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
//            assertEquals(getterService.getBitmapIndex(1), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
//            assertEquals(getterService.getBitmapIndex(2), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
//            assertEquals(getterService.getBitmapIndex(3), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
//            assertEquals(getterService.getBitmapIndex(4), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
//            assertEquals(getterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 2, 3, 5}));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void buildEmptyTablePath() {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tablePathIndexBuilder = new BITablePathIndexBuilder(cube, null, BICubePathTestTool.getEmptyTable_B_C());
        BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
        transportTest.transport(BIMemoryDataSourceFactory.generateEmptyTable());
        transportTest.transport(BIMemoryDataSourceFactory.generateTableB());
        transportTest.transport(BIMemoryDataSourceFactory.generateTableC());

        BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateEmptyTable(), 1);
        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableC(), 2);
        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableB(), 1);
        fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableB(), 2);

        BIRelationIndexBuilderTest relationIndexBuilder = new BIRelationIndexBuilderTest();
        relationIndexBuilder.setTableA(BIMemoryDataSourceFactory.generateEmptyTable());
        relationIndexBuilder.setTableB(BIMemoryDataSourceFactory.generateTableB());
        relationIndexBuilder.generateRelationIndex(BICubeRelationTestTool.getEmptyTable_Tb(),
                BIMemoryDataSourceFactory.generateEmptyTable(),
                BIMemoryDataSourceFactory.generateTableB(), 1, 2);

        relationIndexBuilder.setTableA(BIMemoryDataSourceFactory.generateTableB());
        relationIndexBuilder.setTableB(BIMemoryDataSourceFactory.generateTableC());
        relationIndexBuilder.generateRelationIndex(BICubeRelationTestTool.getTbTc(), BIMemoryDataSourceFactory.generateTableB(),
                BIMemoryDataSourceFactory.generateTableC(), 1, 2);
        tablePathIndexBuilder.mainTask(null);
    }
}

class BITablePathBuilderTestPackageConfigurationManager extends BISystemPackageConfigurationManager {

    @Override
    public Set<IBusinessPackageGetterService> getAllPackages(long userId) {
        Set<IBusinessPackageGetterService> set = new HashSet<IBusinessPackageGetterService>();
        IBusinessPackageGetterService businessPackageGetterService = new BITablePathBuilderTestPackage(new BIPackageID("1"));
        set.add(businessPackageGetterService);
        return set;
    }
}

class BITablePathBuilderTestPackage extends BIBasicBusinessPackage {

    public BITablePathBuilderTestPackage(BIPackageID id) {
        super(id);
    }

    public Set<BusinessTable> getBusinessTables() {
        Set<BusinessTable> set = new HashSet<BusinessTable>();
        BusinessTable businessTable1 = new BIBusinessTable("1","emptyTable");
        businessTable1.setSource(BIMemoryDataSourceFactory.generateEmptyTable());

        BusinessTable businessTable2 = new BIBusinessTable("2","aTable");
        businessTable2.setSource(BIMemoryDataSourceFactory.generateTableA());

        BusinessTable businessTable3 = new BIBusinessTable("3","bTable");
        businessTable3.setSource(BIMemoryDataSourceFactory.generateTableB());

        BusinessTable businessTable4 = new BIBusinessTable("4","cTable");
        businessTable4.setSource(BIMemoryDataSourceFactory.generateTableC());
        set.add(businessTable1);
        set.add(businessTable2);
        set.add(businessTable3);
        set.add(businessTable4);
        return set;
    }
}
