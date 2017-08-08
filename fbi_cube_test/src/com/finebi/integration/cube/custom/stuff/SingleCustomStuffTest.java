package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.impl.conf.CubeBuildCustomStuff;
import com.finebi.integration.cube.custom.stuff.creater.TableSourceCreater;
import com.finebi.integration.cube.custom.stuff.creater.WholeCreater;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 * 基础表:A、B、C、D
 * ETL表:AB、CD、AB_CD
 * relation:AB、BC、CD
 * path:ABC、BCD、ABCD
 */
public class SingleCustomStuffTest extends AbstractCustomStuffTest {

    public void testEmpty() {
        clearAll();
        CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
        assertTrue(cubeBuildCustomStuff.getSingleSourceLayers().isEmpty());
        assertTrue(cubeBuildCustomStuff.getDependTableResource().isEmpty());

        assertTrue(cubeBuildCustomStuff.getTableSourceRelationSet().isEmpty());
        assertTrue(cubeBuildCustomStuff.getCubeGenerateRelationSet().isEmpty());

        assertTrue(cubeBuildCustomStuff.getTableSourceRelationPathSet().isEmpty());
        assertTrue(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().isEmpty());
    }

    //更新A表，所有表和关联均缺失，
    //更新表A。
    public void testSingleA() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            Set<String> set = new HashSet<String>();
            set.add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceA, set);
            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            tableSources.add(tableSourceA);
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 1);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 1);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    //更新A表，B表存在，其他缺失。
    //更新A、AB表、AB关联
    public void testSingleAWithBExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceAB);

            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());

            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);

            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceB());

            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 2);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    //更新A表，BC存在，其他缺失
    //更新A、AB表、AB关联
    public void testSingleAWithBCExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceAB);

            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());

            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);

            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceB());
            absentTables.remove(TableSourceCreater.getTableSourceC());

            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 2);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    //更新A表，BCD存在，其他缺失
    //更新A、AB表、CD表、AB_CD表、AB关联
    public void testSingleAWithBCDExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            CubeTableSource tableSourceAB_CD = TableSourceCreater.getTableSourceAB_CD();
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceAB);
            tableSources.add(tableSourceAB_CD);

            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB_CD, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB_CD).add(tableSourceA.getSourceID());

            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceAB_CD.getSourceID(), 1);

            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceB());
            absentTables.remove(TableSourceCreater.getTableSourceC());
            absentTables.remove(TableSourceCreater.getTableSourceD());

            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 4);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB_CD()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
