package com.finebi.integration.cube.custom.it;

import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.impl.conf.CubeBuildCustomTables;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.base.cube.BISystemCubeConfManager;
import com.fr.bi.conf.manager.update.BIUpdateSettingManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIUpdateFrequencyManagerProvider;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.*;

/**
 * Created by Lucifer on 2017-3-20.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 * 单表A、B、C、D
 * ETL表AB、BC、CD
 */
public class CustomTableTest extends TestCase {

    Map<CubeTableSource, Set<String>> tableBaseSourceIdMap;
    Map<String, Set<CubeTableSource>> baseSourceIdTableMap;
    Map<String, Set<Integer>> baseSourceIdUpdateTypeMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new CustomPackageProvider());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, new CustomRelationProvider());
        StableFactory.registerMarkedObject(BISystemCubeConfManager.XML_TAG, new CustomCubeConfManager());
        StableFactory.registerMarkedObject(BIDataSourceManagerProvider.XML_TAG, new CustomDataSourceManager());
        StableFactory.registerMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, new BIUpdateSettingManager());
    }

    //单表更新A,同时更新ETL表AB,不更新B表。
    public void testCustomTableA() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);
        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap,
                new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、AB
        assertEquals(stuff.getDependTableResource().size(), 2);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        //A、AB
        assertEquals(set.size(), 2);
        //A、B、AB
        assertEquals(stuff.getSingleSourceLayers().size(), 3);

        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 3);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 1);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 1);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 1);
    }

    //单表更新A、B，同时更新AB、BC，不更新C表
    public void testCustomTableAB() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);

        baseTableSourceIds.add(CustomTableCreater.getTableSourceB().getSourceID());
        updateTypes.add(2);

        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap,
                new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、B、AB、AC表
        assertEquals(stuff.getDependTableResource().size(), 4);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        //A、AB
        assertEquals(set.size(), 4);
        //A、B、AB
        assertEquals(stuff.getSingleSourceLayers().size(), 5);

        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 5);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceB()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceBC()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceC()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 2);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 2);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 2);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 2);
    }

    //单表更新A、B、C，同时更新AB、BC、CD，不更新D表
    public void testCustomTableABC() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceB().getSourceID());
        updateTypes.add(2);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceC().getSourceID());
        updateTypes.add(1);
        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap, new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、B、C、AB、BC、CD表
        assertEquals(stuff.getDependTableResource().size(), 6);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        assertEquals(set.size(), 6);
        assertEquals(stuff.getSingleSourceLayers().size(), 7);
        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 7);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceB()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceC()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceD()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceBC()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceCD()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 2);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 2);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 3);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 2);
    }

    //单表更新A、D，同时更新AB、BC、CD，不更新B、C表
    public void testCustomTableAD() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceD().getSourceID());
        updateTypes.add(2);

        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap,
                new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、D、AB、CD表
        assertEquals(stuff.getDependTableResource().size(), 4);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        assertEquals(set.size(), 4);
        assertEquals(stuff.getSingleSourceLayers().size(), 6);

        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 6);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceC()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceD()).getUpdateType(), 2);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceCD()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 1);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 1);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 1);
    }

    //单表更新A 增量、全量、从不，合并后只更新全量
    public void testCustomTableAAA() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(2);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(0);

        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap,
                new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、AB表
        assertEquals(stuff.getDependTableResource().size(), 2);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        assertEquals(set.size(), 2);
        assertEquals(stuff.getSingleSourceLayers().size(), 3);

        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 3);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 0);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 1);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 1);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 1);
    }

    //单表更新A 增量、从不，合并后只更新增量
    public void testCustomTableAA() {
        tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        baseSourceIdTableMap = new HashMap<String, Set<CubeTableSource>>();
        baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();
        List<String> baseTableSourceIds = new ArrayList<String>();
        List<Integer> updateTypes = new ArrayList<Integer>();
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(1);
        baseTableSourceIds.add(CustomTableCreater.getTableSourceA().getSourceID());
        updateTypes.add(2);

        CustomTableTestTool.calc(baseTableSourceIds, updateTypes, tableBaseSourceIdMap, baseSourceIdTableMap, baseSourceIdUpdateTypeMap);
        CubeBuildStuff stuff = new CubeBuildCustomTables(UserControl.getInstance().getSuperManagerID(), tableBaseSourceIdMap, baseSourceIdUpdateTypeMap,
                new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        //需要更新的表包括A、AB表
        assertEquals(stuff.getDependTableResource().size(), 2);
        Set<CubeTableSource> set = BIDataStructTranUtils.set2Set(stuff.getDependTableResource());
        assertEquals(set.size(), 2);
        assertEquals(stuff.getSingleSourceLayers().size(), 3);

        Map<CubeTableSource, UpdateSettingSource> updateTypeMap = stuff.getUpdateSettingSources();
        assertEquals(updateTypeMap.size(), 3);
        assertEquals(updateTypeMap.get(CustomTableCreater.getTableSourceA()).getUpdateType(), 1);
        assertEquals(updateTypeMap.get(CustomTableCreater.getSOTableSourceAB()).getUpdateType(), 0);

        assertEquals(stuff.getTableSourceRelationSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationSet().size(), 1);
        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
        for (BICubeGenerateRelation biCubeGenerateRelation : stuff.getCubeGenerateRelationSet()) {
            dependTableSourceSet.addAll(biCubeGenerateRelation.getDependTableSourceSet());
        }
        assertEquals(dependTableSourceSet.size(), 1);

        assertEquals(stuff.getTableSourceRelationPathSet().size(), 1);
        assertEquals(stuff.getCubeGenerateRelationPathSet().size(), 1);
        Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
        for (BICubeGenerateRelationPath biCubeGenerateRelationPath : stuff.getCubeGenerateRelationPathSet()) {
            dependRelationPathSet.addAll(biCubeGenerateRelationPath.getDependRelationPathSet());
        }
        assertEquals(dependRelationPathSet.size(), 1);
    }

}
