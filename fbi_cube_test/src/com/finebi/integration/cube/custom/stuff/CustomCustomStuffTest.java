package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.impl.conf.CubeBuildCustomStuff;
import com.finebi.integration.cube.custom.stuff.creater.TableSourceCreater;
import com.finebi.integration.cube.custom.stuff.creater.WholeCreater;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Map;

/**
 * Created by Lucifer on 2017-6-2.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 * 基础表:A、B、C、D
 * ETL表:AB、CD、AB_CD
 * relation:AB、BC、CD
 * path:ABC、BCD、ABCD
 */
public class CustomCustomStuffTest extends AbstractCustomStuffTest {

    /**
     * 更新AB表，其他表都缺失
     * 更新A表，B表，AB表，AB关联
     */
    public void testCustomABWithNoneExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceB = TableSourceCreater.getTableSourceB();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceB).add(tableSourceB.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceB.getSourceID());
            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceB.getSourceID(), 2);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceB);
            tableSources.add(tableSourceAB);
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 3);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * 更新AB表，C表存在，其他缺失
     * 更新A表，B表，AB表，AB、BC关联和ABC路径
     */
    public void testCustomABWithCExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceB = TableSourceCreater.getTableSourceB();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceB).add(tableSourceB.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceB.getSourceID());
            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceB.getSourceID(), 2);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceB);
            tableSources.add(tableSourceAB);
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceC());
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 3);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 1);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * 更新AB表，CD表存在，其他缺失
     * 更新A表，B表，AB表,CD表,AB_CD表，AB、BC关联和ABC路径
     */
    public void testCustomABWithCDExist() {
        try {
            clearAll();
            CubeTableSource tableSourceA = TableSourceCreater.getTableSourceA();
            CubeTableSource tableSourceB = TableSourceCreater.getTableSourceB();
            CubeTableSource tableSourceAB = TableSourceCreater.getTableSourceAB();
            CubeTableSource tableSourceAB_CD = TableSourceCreater.getTableSourceAB_CD();
            tableBaseSourceIdMap.put(tableSourceA, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceA).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.put(tableSourceB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceB).add(tableSourceB.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.get(tableSourceAB).add(tableSourceB.getSourceID());
            tableBaseSourceIdMap.put(tableSourceAB_CD, new HashSet<String>());
            tableBaseSourceIdMap.get(tableSourceAB_CD).add(tableSourceA.getSourceID());
            tableBaseSourceIdMap.get(tableSourceAB_CD).add(tableSourceB.getSourceID());
            sourceIdUpdateTypeMap.put(tableSourceA.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceB.getSourceID(), 2);
            sourceIdUpdateTypeMap.put(tableSourceAB.getSourceID(), 1);
            sourceIdUpdateTypeMap.put(tableSourceAB_CD.getSourceID(), 1);
            tableSources.add(tableSourceA);
            tableSources.add(tableSourceB);
            tableSources.add(tableSourceAB);
            tableSources.add(tableSourceAB_CD);
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceC());
            absentTables.remove(TableSourceCreater.getTableSourceD());
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 5);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 1);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceCD()).getUpdateType(), 0);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB_CD()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * 更新ABC表，其他缺失
     * 更新A表，B表，C表，AB表，AB关联,BC关联，ABC路径
     */
    public void testCustomABCWithNoneExist() {
        try {
            clearAll();
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceA(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceA()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceC(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceC()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceAB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceA().getSourceID(), 1);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceB().getSourceID(), 2);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceC().getSourceID(), 2);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceAB().getSourceID(), 1);
            tableSources.add(TableSourceCreater.getTableSourceA());
            tableSources.add(TableSourceCreater.getTableSourceB());
            tableSources.add(TableSourceCreater.getTableSourceC());
            tableSources.add(TableSourceCreater.getTableSourceAB());
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 4);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 4);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 2);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 1);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 1);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceC()).getUpdateType(), 2);
            assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * 更新ABC表，D表存在，其他缺失
     * 更新A表，B表，C表，AB表，CD表，AB_CD表,AB关联,BC关联，CD关联，ABC路径，BCD路径，ABCD路径
     */
    public void testCustomABCWithDExist() {
        try {
            clearAll();
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceA(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceA()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceC(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceC()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceAB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceCD(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceCD()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceAB_CD(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceC().getSourceID());
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceA().getSourceID(), 1);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceB().getSourceID(), 2);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceC().getSourceID(), 2);
            tableSources.add(TableSourceCreater.getTableSourceA());
            tableSources.add(TableSourceCreater.getTableSourceB());
            tableSources.add(TableSourceCreater.getTableSourceC());
            tableSources.add(TableSourceCreater.getTableSourceAB());
            tableSources.add(TableSourceCreater.getTableSourceCD());
            tableSources.add(TableSourceCreater.getTableSourceAB_CD());
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceD());
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertCustomABCWithDExist(cubeBuildCustomStuff);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void assertCustomABCWithDExist(CubeBuildCustomStuff cubeBuildCustomStuff) {
        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
        assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 6);
        assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
        assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceC()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceCD()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB_CD()).getUpdateType(), 0);
    }

    /**
     * 更新ABCD表，其他缺失
     * 更新A表，B表，C表，AB表，CD表，AB_CD表,AB关联,BC关联，CD关联，ABC路径，BCD路径，ABCD路径
     */
    public void testCustomABCDWithNoneExist() {
        try {
            clearAll();
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceA(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceA()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceC(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceC()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceD(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceD()).add(TableSourceCreater.getTableSourceD().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceAB(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceCD(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceCD()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceCD()).add(TableSourceCreater.getTableSourceD().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceAB_CD(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceB().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceC().getSourceID());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceAB_CD()).add(TableSourceCreater.getTableSourceD().getSourceID());
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceA().getSourceID(), 1);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceB().getSourceID(), 2);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceC().getSourceID(), 2);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceD().getSourceID(), 1);
            tableSources.add(TableSourceCreater.getTableSourceA());
            tableSources.add(TableSourceCreater.getTableSourceB());
            tableSources.add(TableSourceCreater.getTableSourceC());
            tableSources.add(TableSourceCreater.getTableSourceD());
            tableSources.add(TableSourceCreater.getTableSourceAB());
            tableSources.add(TableSourceCreater.getTableSourceCD());
            tableSources.add(TableSourceCreater.getTableSourceAB_CD());
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertCustomABCDWithNoneExist(cubeBuildCustomStuff);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void assertCustomABCDWithNoneExist(CubeBuildCustomStuff cubeBuildCustomStuff) {
        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
        assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 7);
        assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
        assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
        assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceB()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceC()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceD()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceCD()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(TableSourceCreater.getSOTableSourceAB_CD()).getUpdateType(), 0);
    }


    /**
     * 更新AC表，其他缺失。
     * 只更新A表和C表
     */
    public void testCustomACWithNoneExist() {
        try {
            clearAll();
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceA(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceA()).add(TableSourceCreater.getTableSourceA().getSourceID());
            tableBaseSourceIdMap.put(TableSourceCreater.getTableSourceC(), new HashSet<String>());
            tableBaseSourceIdMap.get(TableSourceCreater.getTableSourceC()).add(TableSourceCreater.getTableSourceC().getSourceID());
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceA().getSourceID(), 1);
            sourceIdUpdateTypeMap.put(TableSourceCreater.getTableSourceC().getSourceID(), 2);
            tableSources.add(TableSourceCreater.getTableSourceA());
            tableSources.add(TableSourceCreater.getTableSourceC());
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);

            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 2);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 2);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 0);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 0);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceA()).getUpdateType(), 1);
            assertEquals(updateTypeMap.get(TableSourceCreater.getTableSourceC()).getUpdateType(), 2);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
