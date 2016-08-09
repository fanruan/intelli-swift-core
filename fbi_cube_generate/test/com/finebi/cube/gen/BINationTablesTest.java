package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.gen.oper.BIRelationIndexGenerator;
import com.finebi.cube.gen.oper.BISourceDataAllTransport;
import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.tools.BINationDataFactory;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by kary on 16/5/17.
 */
public class BINationTablesTest extends BICubeTestBase {
    private BISourceDataTransport dataTransport;
    private Set<CubeTableSource> cubeTableSourceSet;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public BINationTablesTest() throws Exception {
        super.setUp();
       
        init();
    }

    private void init() {
        CubeTableSource tableNation = BINationDataFactory.createTableNation();
        CubeTableSource tablePerson = BINationDataFactory.createTablePerson();
        this.cubeTableSourceSet=new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(tableNation);
        cubeTableSourceSet.add(tablePerson);
    }


    public int getTablesAmount() {
        return cubeTableSourceSet.size();
    }

    public void testFieldPathIndex() {
        try {

            /*写入表信息*/
            transport(BINationDataFactory.createTableNation());
            transport(BINationDataFactory.createTablePerson());
            //生成自身索引
            fieldIndexGenerator(BINationDataFactory.createTableNation(), 0);
            fieldIndexGenerator(BINationDataFactory.createTablePerson(), 1);
            fieldIndexGenerator(BINationDataFactory.createTablePerson(), 2);
            //生成依赖关系
            BIRelationIndexGenerator indexGenerator = new BIRelationIndexGenerator(cube, generatePersonsAndNationsRelation());
            indexGenerator.mainTask(null);


            //relations集合,当relations.siez>2时使用
            BICubeTablePath biCubeTablePath = getAllRelations();

            //测试relation
            CubeRelationEntityGetterService iCubeRelationEntityGetterService = cube.getCubeRelation(BITableKeyUtils.convert(BINationDataFactory.createTablePerson()), biCubeTablePath);
            assertEquals(iCubeRelationEntityGetterService.getBitmapIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0}));
            assertEquals(iCubeRelationEntityGetterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));

            //根据value查找索引
            final CubeColumnReaderService iCubeColumnReaderService = cube.getCubeColumn(BITableKeyUtils.convert(BINationDataFactory.createTablePerson()), BIColumnKey.covertColumnKey(new BICubeFieldSource(BITableSourceTestTool.getDBTableSourcePerson(), "name", DBConstant.CLASS.STRING, 255)));

            //获取本表对应位置索引值
            assertEquals(iCubeColumnReaderService.getIndexByGroupValue("nameA"), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 2}));
            //根据行号(rowId来查询value
            assertEquals(iCubeColumnReaderService.getOriginalObjectValueByRow(1), "nameB");


            //select rowId from persons where name='nameA'
            GroupValueIndex indexByGroupValue = iCubeColumnReaderService.getIndexByGroupValue("nameA");
            final List<Integer> ids = new ArrayList<Integer>();
            indexByGroupValue.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    ids.add(row);
                }
            });
            assertTrue(ComparatorUtils.equals(ids.toArray(), new int[]{0, 2}));


            //select name from persons where rowId in (0,1)
            final List<String> idList = new ArrayList<String>();
            idList.add((String) iCubeColumnReaderService.getOriginalObjectValueByRow(0));
            idList.add((String) iCubeColumnReaderService.getOriginalObjectValueByRow(1));
            assertTrue(ComparatorUtils.equals(idList.toArray(), new String[]{"nameA", "nameB"}));


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }


    /**
     * 写入
     */
    public void transport(CubeTableSource tableSource) {
        try {
            dataTransport = new BISourceDataAllTransport(cube, tableSource, new HashSet<CubeTableSource>(), new HashSet<CubeTableSource>(),1);
            dataTransport.mainTask(null);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 生成索引
     */
    public void fieldIndexGenerator(CubeTableSource tableSource, int columnIndex) {
        try {
            setUp();
            BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
            transportTest.transport(tableSource);
            ICubeFieldSource field = tableSource.getFieldsArray(null)[columnIndex];
            Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
            while (columnKeyIterator.hasNext()) {
                BIColumnKey columnKey = columnKeyIterator.next();
                BIFieldIndexGenerator fieldIndexGenerator = new BIFieldIndexGenerator(cube, tableSource, tableSource.getFieldsArray(null)[columnIndex], columnKey);
                fieldIndexGenerator.mainTask(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 生成relations的集合
     */
    protected BICubeTablePath getAllRelations() throws BITablePathConfusionException {
        BICubeTablePath path = new BICubeTablePath();

        path.addRelationAtHead(generatePersonsAndNationsRelation());
        return path;
    }

    /**
     * 生成relation
     */
    protected BICubeRelation generatePersonsAndNationsRelation() throws BITablePathConfusionException {
        CubeTableSource persons;
        CubeTableSource nations;
        nations = BINationDataFactory.createTableNation();
        persons = BINationDataFactory.createTablePerson();
        BICubeRelation biCubeRelation = new BICubeRelation(
                BIColumnKey.covertColumnKey(new BICubeFieldSource(persons, "nationId", DBConstant.CLASS.LONG, 255)),
                BIColumnKey.covertColumnKey(new BICubeFieldSource(nations, "id", DBConstant.CLASS.LONG, 255)),
                BITableKeyUtils.convert(persons),
                BITableKeyUtils.convert(nations));
        return biCubeRelation;
    }


}
