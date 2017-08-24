package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.impl.conf.CubeBuildCustomStuff;
import com.finebi.integration.cube.custom.stuff.creater.TableSourceCreater;
import com.finebi.integration.cube.custom.stuff.creater.WholeCreater;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.data.source.CubeTableSource;

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
public class PartCustomStuffTest extends AbstractCustomStuffTest {

    public void testPartWithNoneExist() {
        try {
            clearAll();
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            tableSources.addAll(absentTables);
            relations.addAll(absentRelations);
            paths.addAll(absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 7);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            for (UpdateSettingSource updateSettingSource : updateTypeMap.values()) {
                assertEquals(updateSettingSource.getUpdateType(), 0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testPartWithAExist() {
        try {
            clearAll();
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceA());

            tableSources.addAll(absentTables);
            relations.addAll(absentRelations);
            paths.addAll(absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 6);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            for (UpdateSettingSource updateSettingSource : updateTypeMap.values()) {
                assertEquals(updateSettingSource.getUpdateType(), 0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testPartWithABExist() {
        try {
            clearAll();
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceA());
            absentTables.remove(TableSourceCreater.getTableSourceB());
            tableSources.addAll(absentTables);
            relations.addAll(absentRelations);
            paths.addAll(absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 5);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            for (UpdateSettingSource updateSettingSource : updateTypeMap.values()) {
                assertEquals(updateSettingSource.getUpdateType(), 0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testPartWithABCExist() {
        try {
            clearAll();
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceA());
            absentTables.remove(TableSourceCreater.getTableSourceB());
            absentTables.remove(TableSourceCreater.getTableSourceC());
            tableSources.addAll(absentTables);
            relations.addAll(absentRelations);
            paths.addAll(absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 4);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            for (UpdateSettingSource updateSettingSource : updateTypeMap.values()) {
                assertEquals(updateSettingSource.getUpdateType(), 0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testPartWithABCDExist() {
        try {
            clearAll();
            WholeCreater.abcentAll(absentTables, absentRelations, absentPaths);
            absentTables.remove(TableSourceCreater.getTableSourceA());
            absentTables.remove(TableSourceCreater.getTableSourceB());
            absentTables.remove(TableSourceCreater.getTableSourceC());
            absentTables.remove(TableSourceCreater.getTableSourceD());
            tableSources.addAll(absentTables);
            relations.addAll(absentRelations);
            paths.addAll(absentPaths);
            CubeBuildCustomStuff cubeBuildCustomStuff = new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources, relations, paths, absentTables, absentRelations, absentPaths);
            assertTrue(cubeBuildCustomStuff != null);
            assertEquals(BIDataStructTranUtils.set2Set(cubeBuildCustomStuff.getDependTableResource()).size(), 3);
            assertEquals(cubeBuildCustomStuff.getSingleSourceLayers().size(), 7);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getTableSourceRelationPathSet().size(), 3);
            assertEquals(cubeBuildCustomStuff.getCubeGenerateRelationPathSet().size(), 3);
            Map<CubeTableSource, UpdateSettingSource> updateTypeMap = cubeBuildCustomStuff.getUpdateSettingSources();
            for (UpdateSettingSource updateSettingSource : updateTypeMap.values()) {
                assertEquals(updateSettingSource.getUpdateType(), 0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
