package com.finebi.cube.impl.conf;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by Lucifer on 2017-3-10.
 *
 * @author Lucifer
 * @since 4.0
 */
public class CubeBuildCustomTables extends CubeBuildSpecific {

    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildCustomTables.class);
    private Set<CubeTableSource> specificTables = new HashSet<CubeTableSource>();
    private Set<String> sourceIds = new HashSet<String>();
    private Set<String> specificBasicTableIDs = new HashSet<String>();

    private Map<String, Set<CubeTableSource>> baseSourceIdTableMap;
    private Map<String, Set<Integer>> baseSourceIdUpdateTypeMap;

    public CubeBuildCustomTables(long userId,
                                 Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                                 Map<String, Set<CubeTableSource>> baseSourceIdTableMap,
                                 Map<String, Set<Integer>> baseSourceIdUpdateTypeMap,
                                 Set<CubeTableSource> absentTables,
                                 Set<BITableSourceRelation> absentRelation,
                                 Set<BITableSourceRelationPath> absentPath) {
        super(userId);
        this.baseSourceIdTableMap = baseSourceIdTableMap;
        this.baseSourceIdUpdateTypeMap = baseSourceIdUpdateTypeMap;

        for (Map.Entry<CubeTableSource, Set<String>> entry : tableBaseSourceIdMap.entrySet()) {
            specificTables.add(entry.getKey());
            specificBasicTableIDs.addAll(entry.getValue());
            sourceIds.add(entry.getKey().getSourceID());
        }

        this.tableInConstruction = new HashSet<CubeTableSource>();
        this.tableInConstruction.addAll(specificTables);

        this.relationInConstruction = calculateRelevantRelation(this.tableInConstruction);
        this.relationInConstruction = removeRelationAbsentTable(this.specificTables, this.relationInConstruction, absentTables);

        this.pathInConstruction = calculateRelevantPath(this.relationInConstruction);
        this.pathInConstruction = removePathAbsentRelation(this.pathInConstruction, absentTables, absentRelation, absentPath);

        filter();
        calculateDepends(this.tableInConstruction, this.relationInConstruction, this.pathInConstruction);

        this.tableSourceLayerDepends = filterBasicTable(tableSourceLayerDepends, specificBasicTableIDs, absentTables);

    }

    private Set<List<Set<CubeTableSource>>> filterBasicTable(Set<List<Set<CubeTableSource>>> tableSourceLayerDepends,
                                                             Set<String> specificBasicTableIDs,
                                                             Set<CubeTableSource> absentTables) {
        Set<String> absentTableIDs = new HashSet<String>();
        for (CubeTableSource tableSource : absentTables) {
            absentTableIDs.add(tableSource.getSourceID());
        }
        /**
         * 需要更新的ID
         */
        Set<String> etlStepIDs = new HashSet<String>();
        etlStepIDs.addAll(specificBasicTableIDs);
        etlStepIDs.addAll(sourceIds);

        Iterator<List<Set<CubeTableSource>>> iterator = tableSourceLayerDepends.iterator();
        while (iterator.hasNext()) {
            List<Set<CubeTableSource>> tableLayers = iterator.next();
            Iterator<Set<CubeTableSource>> tableLayerIterator = tableLayers.iterator();
            while (tableLayerIterator.hasNext()) {
                Set<CubeTableSource> oneLayer = tableLayerIterator.next();
                Iterator<CubeTableSource> it = oneLayer.iterator();
                while (it.hasNext()) {
                    CubeTableSource tableSource = it.next();
                    /**
                     * 是基础表，并且在需要生成的表中，则生成。否则移除。
                     */
                    if (TableSourceUtils.isBasicTable(tableSource)) {
                        if (!etlStepIDs.contains(tableSource.getSourceID())) {
                            it.remove();
                        }
                    }
                }
                if (oneLayer.isEmpty()) {
                    tableLayerIterator.remove();
                }
            }
        }
        return tableSourceLayerDepends;
    }

    private Set<BITableSourceRelation> removeRelationAbsentTable(Set<CubeTableSource> specificTables, Set<BITableSourceRelation> relationInConstruction, Set<CubeTableSource> absentTable) {
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        Set<String> absentTableIDs = new HashSet<String>();
        for (CubeTableSource table : absentTable) {
            absentTableIDs.add(table.getSourceID());
        }
        for (BITableSourceRelation relation : relationInConstruction) {
            for (CubeTableSource specificTable : specificTables) {
                if (ComparatorUtils.equals(relation.getPrimaryTable().getSourceID(), specificTable.getSourceID())) {
                    if (!absentTableIDs.contains(relation.getForeignTable().getSourceID())) {
                        result.add(relation);
                    }
                } else if (ComparatorUtils.equals(relation.getForeignTable().getSourceID(), specificTable.getSourceID())) {
                    if (!absentTableIDs.contains(relation.getPrimaryTable().getSourceID())) {
                        result.add(relation);
                    }
                }
            }
        }
        return result;
    }

    private Set<BITableSourceRelationPath> removePathAbsentRelation(Set<BITableSourceRelationPath> pathInConstruction,
                                                                    Set<CubeTableSource> absentTable,
                                                                    Set<BITableSourceRelation> absentRelation,
                                                                    Set<BITableSourceRelationPath> absentPath) {
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        Set<String> relationIDInConstruction = new HashSet<String>();
        Set<String> relationIDAbsent = new HashSet<String>();
        Set<String> pathIDsInConstruction = new HashSet<String>();
        Set<String> pathIDsAbsent = new HashSet<String>();
        Set<String> absentTableIDs = new HashSet<String>();
        for (CubeTableSource table : absentTable) {
            absentTableIDs.add(table.getSourceID());
        }
        for (BITableSourceRelationPath path : absentPath) {
            pathIDsAbsent.add(BIRelationIDUtils.calculatePathID(path));
        }
        for (BITableSourceRelation relation : this.relationInConstruction) {
            relationIDInConstruction.add(BIRelationIDUtils.calculateRelationID(relation));
        }
        for (BITableSourceRelation relation : absentRelation) {
            relationIDAbsent.add(BIRelationIDUtils.calculateRelationID(relation));
        }
        try {
            List<BITableSourceRelationPath> sortPath = sortPath(pathInConstruction);
            for (BITableSourceRelationPath path : sortPath) {
                if (path.size() >= 2) {
                    /**
                     * 当前路径的最后一条关联，在构建的时候存在
                     */
                    if (checklastRelation(path.getLastRelation(), absentTableIDs, relationIDInConstruction, relationIDAbsent)) {
                        /**
                         * 获得当前路径的前部。也就是移除最后一条关联的剩余部分。
                         */
                        BITableSourceRelationPath pathFront = getFrontPath(path);
                        /**
                         * 路径的前部长度为1的话，那么当做是关联处理。
                         */
                        if (pathFront.size() == 1) {
                            if (isRelationExistWhenBuild(pathFront.getFirstRelation(), relationIDInConstruction, relationIDAbsent)
                                    ) {
                                /**
                                 * 路径的前后部分，都是存在，那么当前路径可以构建。
                                 */
                                result.add(path);
                                pathIDsInConstruction.add(BIRelationIDUtils.calculatePathID(path));
                            }
                        } else {
                            /**
                             * 路径的前部长度依然为一条长度大于1的路径。
                             */
                            if (isPathExistWhenBuild(pathFront, pathIDsInConstruction, pathIDsAbsent)) {
                                /**
                                 * 路径的前后部分，都是存在，那么当前路径可以构建。
                                 */
                                result.add(path);
                                pathIDsInConstruction.add(BIRelationIDUtils.calculatePathID(path));
                            }
                        }
                    }
                }
            }
        } catch (BITablePathEmptyException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    private boolean checklastRelation(BITableSourceRelation sourceRelation, Set<String> absentTableIDs, Set<String> relationIDInConstruction, Set<String> relationIDAbsent) {
        boolean relationExist = isRelationExistWhenBuild(sourceRelation, relationIDInConstruction, relationIDAbsent);

        boolean foreignTableExist = !absentTableIDs.contains(sourceRelation.getForeignTable().getSourceID()) ||
                this.sourceIds.contains(sourceRelation.getForeignTable().getSourceID());
        return relationExist && foreignTableExist;
    }

    private BITableSourceRelationPath getFrontPath(BITableSourceRelationPath path) {
        try {
            BITableSourceRelationPath pathFront = new BITableSourceRelationPath();
            pathFront.copyFrom(path);
            pathFront.removeLastRelation();
            return pathFront;
        } catch (BITablePathEmptyException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private List<BITableSourceRelationPath> sortPath(Set<BITableSourceRelationPath> pathInConstruction) {
        List<BITableSourceRelationPath> result = new ArrayList<BITableSourceRelationPath>();
        for (BITableSourceRelationPath path : pathInConstruction) {
            result.add(path);
        }
        Collections.sort(result, new Comparator<BITableSourceRelationPath>() {
            @Override
            public int compare(BITableSourceRelationPath o1, BITableSourceRelationPath o2) {
                return o1.size() - o2.size();
            }
        });
        return result;
    }

    /**
     * 在生成的时候，当前关联是否存在。
     * 判断逻辑：
     * 关联此次被构建，或者关联在cube中存在。
     *
     * @param relation
     * @param relationIDInConstruction
     * @param relationIDAbsent
     * @return
     */
    private boolean isRelationExistWhenBuild(BITableSourceRelation relation, Set<String> relationIDInConstruction, Set<String> relationIDAbsent) {
        String relationID = BIRelationIDUtils.calculateRelationID(relation);
        return relationIDInConstruction.contains(relationID) || !relationIDAbsent.contains(relationID);
    }

    /**
     * 在生成的时候，当前路径是否存在。
     * 判断逻辑：
     * 路径此次被构建，或者路径在cube中存在。
     *
     * @param path
     * @param pathIDInConstruction
     * @param pathIDAbsent
     * @return
     */
    private boolean isPathExistWhenBuild(BITableSourceRelationPath path, Set<String> pathIDInConstruction, Set<String> pathIDAbsent) {
        if (path.size() >= 2) {
            String pathID = BIRelationIDUtils.calculatePathID(path);
            return pathIDInConstruction.contains(pathID) || !pathIDAbsent.contains(pathID);
        } else {
            return false;
        }
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {

        Map<CubeTableSource, UpdateSettingSource> map = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : this.getSingleSourceLayers()) {
            UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(source.getSourceID(), userId);
            if (updateSettingSource == null) {
                updateSettingSource = new UpdateSettingSource();
            }

            if (specificBasicTableIDs.contains(source.getSourceID())) {
                Set<Integer> updateTypeSet = baseSourceIdUpdateTypeMap.get(source.getSourceID());
                updateSettingSource.setUpdateType(calcUpdateType(updateTypeSet));
            }

            if (source.getType() == BIBaseConstant.TABLETYPE.ETL) {
                updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
            }
            map.put(source, updateSettingSource);
        }
        return map;
    }

    /**
     * 优先级 all>part>never
     *
     * @param updateTypeSet
     * @return
     */
    private int calcUpdateType(Set<Integer> updateTypeSet) {
        if (updateTypeSet == null || updateTypeSet.isEmpty()) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
        } else if (updateTypeSet.contains(DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART)) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART;
        } else if (updateTypeSet.contains(DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER)) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER;
        } else {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
        }
    }

    @Override
    public String getCubeTaskId() {
        Object[] objects = specificBasicTableIDs.toArray();
        String[] ids = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            ids[i] = (String) objects[i];
        }
        return BIStringUtils.append(ids);
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.SINGLE;
    }
}
