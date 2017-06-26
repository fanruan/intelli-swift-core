package com.fr.bi.cal.generate.task.calculator;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.ITaskCalculator;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildCustomStuff;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.cal.generate.CustomTaskBuilder;
import com.fr.bi.cal.generate.task.CustomCubeGenerateTask;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.util.BIConfUtils;

import java.util.*;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTaskCalculator implements ITaskCalculator {

    protected CustomCubeGenerateTask customCubeGenerateTask;

    protected long userId;
    //暂时只支持基础表。基础表id、updateType对应关系。
    private Map<String, List<Integer>> baseSourceIdUpdateTypeMap;
    private List<BITableSourceRelation> tableSourceRelations;

    protected Set<CubeTableSource> tableSources = new HashSet<CubeTableSource>();
    protected Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
    protected Set<BITableSourceRelationPath> paths = new HashSet<BITableSourceRelationPath>();
    protected Set<CubeTableSource> absentTables;
    protected Set<BITableSourceRelation> absentRelations;
    protected Set<BITableSourceRelationPath> absentPaths;

    public CustomTaskCalculator(CustomCubeGenerateTask customCubeGenerateTask) {
        this.customCubeGenerateTask = customCubeGenerateTask;
        this.userId = customCubeGenerateTask.getUserId();

        this.baseSourceIdUpdateTypeMap = customCubeGenerateTask.getSourceIdUpdateTypeMap();
        this.tableSourceRelations = BIConfUtils.convert2TableSourceRelation(customCubeGenerateTask.getTableRelations());
    }

    @Override
    public CubeBuildStuff generateCubeBuildStuff(Set<CubeTableSource> allTableSources,
                                                 Set<BITableSourceRelation> allRelations, Set<BITableSourceRelationPath> allPaths) {
        //所有tablesource和基础表sourceId对应关系。
        Map<CubeTableSource, Set<String>> tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        //所有sourceId和updateType对应关系
        Map<String, Integer> sourceIdUpdateTypeMap = new HashMap<String, Integer>();

        if (baseSourceIdUpdateTypeMap != null && !baseSourceIdUpdateTypeMap.isEmpty()) { //单表、自定义等更新。
            calculateTableSources(tableBaseSourceIdMap, sourceIdUpdateTypeMap);
            relations.addAll(tableSourceRelations);
        } else { //check更新。
            tableSources = CubeUpdateUtils.getAllCubeAbsentTables(userId);
            relations = CubeUpdateUtils.getCubeAbsentRelations(userId);
            paths = CubeUpdateUtils.getCubeAbsentPaths(userId);
        }

        absentTables = CubeUpdateUtils.getAllCubeAbsentTables(userId);
        absentRelations = CubeUpdateUtils.getCubeAbsentRelations(userId);
        absentPaths = CubeUpdateUtils.getCubeAbsentPaths(userId);
        return new CubeBuildCustomStuff(userId, tableBaseSourceIdMap, sourceIdUpdateTypeMap, tableSources,
                relations, paths, absentTables, absentRelations, absentPaths,
                allTableSources, allRelations, allPaths);
    }

    /**
     * @param tableBaseSourceIdMap
     * @param sourceIdUpdateTypeMap
     */
    private void calculateTableSources(Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                                       Map<String, Integer> sourceIdUpdateTypeMap) {
        Set<String> baseSourceIds = baseSourceIdUpdateTypeMap.keySet();
        for (Map.Entry<String, List<Integer>> entry : baseSourceIdUpdateTypeMap.entrySet()) {
            String baseTableSourceId = entry.getKey();
            int updateType = CubeUpdateUtils.calcUpdateType(entry.getValue());
            List<BusinessTable> tableList = CustomTaskBuilder.getBusinessTablesContainsSourceId(userId, baseTableSourceId);
            List<CubeTableSource> tableSourceList = CustomTaskBuilder.getTableSourcesNeedUpdate(tableList, userId, baseSourceIds);

            for (CubeTableSource cubeTableSource : tableSourceList) {
                tableSources.add(cubeTableSource);
                if (tableBaseSourceIdMap.containsKey(cubeTableSource)) {
                    tableBaseSourceIdMap.get(cubeTableSource).add(baseTableSourceId);
                } else {
                    tableBaseSourceIdMap.put(cubeTableSource, new HashSet<String>());
                    tableBaseSourceIdMap.get(cubeTableSource).add(baseTableSourceId);
                }
                if (sourceIdUpdateTypeMap.containsKey(baseTableSourceId)) {
                    int oldType = sourceIdUpdateTypeMap.get(baseTableSourceId);
                    sourceIdUpdateTypeMap.put(baseTableSourceId, CubeUpdateUtils.calcUpdateType(oldType, updateType));
                } else {
                    sourceIdUpdateTypeMap.put(baseTableSourceId, updateType);
                }
            }
        }
    }
}
