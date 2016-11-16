package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * This class created on 2016/11/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeBuildStuffSpecificTable extends CubeBuildSpecific {
    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildStuffSpecificTable.class);
    private CubeTableSource specificTable;
    private String specificBasicTableID;
    private int updateType = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;

    public CubeBuildStuffSpecificTable(long userId,
                                       CubeTableSource specificTable,
                                       String specificBasicTableID,
                                       int updateType,
                                       Set<CubeTableSource> absentTables,
                                       Set<BITableSourceRelation> absentRelation,
                                       Set<BITableSourceRelationPath> absentPath) {
        super(userId);
        this.specificTable = specificTable;
        this.specificBasicTableID = specificBasicTableID;
        this.updateType = updateType;
        this.tableInConstruction = new HashSet<CubeTableSource>();
        this.tableInConstruction.add(specificTable);
        this.relationInConstruction = calculateRelevantRelation(this.tableInConstruction);
        this.relationInConstruction = removeRelationAbsentTable(this.specificTable, this.relationInConstruction, absentTables);
        this.pathInConstruction = calculateRelevantPath(this.relationInConstruction);
        this.pathInConstruction = removePathAbsentRelation(this.pathInConstruction, absentTables, absentRelation, absentPath);
        filter();
        calculateDepends(this.tableInConstruction, this.relationInConstruction, this.pathInConstruction);
        if (specificTable instanceof ETLTableSource) {
            this.tableSourceLayerDepends = filterBasicTable(tableSourceLayerDepends, specificBasicTableID, absentTables);
        }
    }

    /**
     * 过滤关联，关联一端的表此次不更新，并且不存在，那么移除
     *
     * @param relationInConstruction
     * @param absentRelation
     * @return
     */
    private Set<BITableSourceRelation> removeRelationAbsentTable(CubeTableSource specificTable, Set<BITableSourceRelation> relationInConstruction, Set<CubeTableSource> absentTable) {
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        Set<String> absentTableIDs = new HashSet<String>();
        for (CubeTableSource table : absentTable) {
            absentTableIDs.add(table.getSourceID());
        }
        for (BITableSourceRelation relation : relationInConstruction) {
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
        return result;
    }

    private Set<BITableSourceRelationPath> removePathAbsentRelation(Set<BITableSourceRelationPath> pathInConstruction,
                                                                    Set<CubeTableSource> absentTable,
                                                                    Set<BITableSourceRelation> absentRelation,
                                                                    Set<BITableSourceRelationPath> absentPath
    ) {
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
                ComparatorUtils.equals(this.specificTable.getSourceID(), sourceRelation.getForeignTable().getSourceID());
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

    protected void checkRelationDepend(BICubeGenerateRelation generateRelation) {
        try {
            if (generateRelation.getDependTableSourceSet().size() == 0) {
                throw BINonValueUtils.beyondControl("the relation should depend one table in specific table build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend none table ");
            } else if (generateRelation.getDependTableSourceSet().size() == 2) {
                StringBuffer sb = new StringBuffer("the relation should depend one table in specific table build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend too many tables,the table size is: " + generateRelation.getDependTableSourceSet().size());
                sb.append("\n tables :");
                Iterator<CubeTableSource> it = generateRelation.getDependTableSourceSet().iterator();
                while (it.hasNext()) {
                    sb.append("\n").append(BuildLogHelper.tableLogContent(it.next()));
                }
                throw BINonValueUtils.beyondControl(sb.toString());
            }
        } catch (Exception e) {
            logger.warn("This is a specific error happened during recording log.It won't disturb main process.The error message:" + e.getMessage(), e);
        }
    }

    /**
     * 如果当前更新的表是ETL表，除了指定的表，其余的基础表都去掉。
     * 除此以外，上级表都不要更新的步骤也不更新。
     *
     * @param tableSourceLayerDepends
     */
    private Set<List<Set<CubeTableSource>>> filterBasicTable(Set<List<Set<CubeTableSource>>> tableSourceLayerDepends, String specificBasicTableID, Set<CubeTableSource> absentTables) {

        Set<String> absentTableIDs = new HashSet<String>();
        for (CubeTableSource tableSource : absentTables) {
            absentTableIDs.add(tableSource.getSourceID());
        }
        /**
         * 需要更新的ID
         */
        Set<String> etlStepIDs = new HashSet<String>();
        etlStepIDs.add(specificBasicTableID);
        etlStepIDs.add(this.specificTable.getSourceID());
        if (tableSourceLayerDepends.size() == 1) {
            List<Set<CubeTableSource>> tableLayers = tableSourceLayerDepends.iterator().next();
            Iterator<Set<CubeTableSource>> tableLayerIterator = tableLayers.iterator();
            while (tableLayerIterator.hasNext()) {
                Set<CubeTableSource> oneLayer = tableLayerIterator.next();
                Iterator<CubeTableSource> it = oneLayer.iterator();
                while (it.hasNext()) {
                    CubeTableSource tableSource = it.next();
                    if (TableSourceUtils.isBasicTable(tableSource)) {
                        /**
                         * 自身是基础表，如包含在需要生成的表中，那么就生成，否则移除。
                         */
                        if (!etlStepIDs.contains(tableSource.getSourceID())) {
                            it.remove();
                        }
                    } else {
                        /**
                         * 如果非基础表，那么都必须生成
                         * 如下场景：
                         * A做了新增公式操作得到A_ETL,然后和B做join操作。
                         * 如果单表对B做更新。此时，不对A进行更新，A表是基础表。
                         * 但是新增公式的操作必须要做，否则没法确定中间的ETL操作是否生成过
                         */
                    }
                }
                if (oneLayer.isEmpty()) {
                    tableLayerIterator.remove();
                }
            }
        } else {
            throw BINonValueUtils.beyondControl("Current operation should specify single table");
        }
        return tableSourceLayerDepends;
    }


    @Override
    public boolean replaceOldCubes() {
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(userId));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(userId));
        try {
            BIFileUtils.moveFile(tempConf.getRootURI().getPath().toString(), advancedConf.getRootURI().getPath().toString());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return true;
    }

    /***
     * 单表更新ETL时，除了选中的表外，其他基础表不作更新
     *
     * @return
     */
    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> map = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : this.getSingleSourceLayers()) {
            UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getTableUpdateSetting(source.getSourceID(), userId);
            if (updateSettingSource == null) {
                updateSettingSource = new UpdateSettingSource();
            }
            if (ComparatorUtils.equals(source.getSourceID(), this.specificBasicTableID)) {
                updateSettingSource.setUpdateType(updateType);
            }
            if (source.getType() == BIBaseConstant.TABLETYPE.ETL) {
                updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
            }
            map.put(source, updateSettingSource);
        }
        return map;
    }

    public String getCubeTaskId() {
        return BIStringUtils.append(DBConstant.CUBE_UPDATE_TYPE.SINGLETABLE_UPDATE, specificTable.getSourceID(), specificBasicTableID);
    }
}
