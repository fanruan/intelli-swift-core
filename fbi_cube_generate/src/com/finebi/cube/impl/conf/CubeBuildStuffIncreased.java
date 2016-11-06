package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.AbstractCubeBuildStuff;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.relation.*;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.File;
import java.util.*;

/**
 * This class created on 2016/11/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeBuildStuffIncreased extends AbstractCubeBuildStuff implements CubeBuildStuff {
    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildStuffIncreased.class);
    private long userId;
    private Set<BIBusinessTable> newlyAddedTables;
    private Set<BITableRelation> newlyAddedRelations;
    private Set<CubeTableSource> tableInConstruction;
    private Set<CubeTableSource> tableLayers;
    private Set<BITableSourceRelation> relationInConstruction;
    private Set<BICubeGenerateRelation> relationDepends;
    private Set<List<Set<CubeTableSource>>> tableSourceLayerDepends;
    private Set<BITableSourceRelationPath> pathInConstruction;
    private Set<BICubeGenerateRelationPath> pathDepends;

    public CubeBuildStuffIncreased(long userId, Set<BIBusinessTable> newlyAddedTables, Set<BITableRelation> newlyAddedRelations) {
        super(userId);
        this.userId = userId;
        this.newlyAddedRelations = new HashSet<BITableRelation>(newlyAddedRelations);
        this.newlyAddedTables = new HashSet<BIBusinessTable>(newlyAddedTables);
        this.relationInConstruction = generateRelationNeedBuild(this.newlyAddedRelations);
        this.tableInConstruction = extractTableSourceInNewlyTable(this.newlyAddedTables);
        this.tableInConstruction.addAll(extractTableSourceInRelation(this.relationInConstruction));
        this.relationDepends = calculateRelationDepends(relationInConstruction);
        this.tableSourceLayerDepends = calculateSourceLayerDepends(tableInConstruction);
        this.tableLayers = calculateSourceLayers(tableSourceLayerDepends);
        this.pathInConstruction = generatePathNeedBuild(this.newlyAddedRelations, this.allRelationPathSet);
        this.pathDepends = calculatePathDepends(this.pathInConstruction, this.relationInConstruction);
    }

    private Set<BICubeGenerateRelationPath> calculatePathDepends(Set<BITableSourceRelationPath> pathInConstruction, Set<BITableSourceRelation> relationInConstruction) {
        return calculateDependTool.calRelationPath(pathInConstruction, relationInConstruction);
    }

    private Set<BITableSourceRelationPath> generatePathNeedBuild(Set<BITableRelation> newlyAddedRelations, Set<BITableRelationPath> allRelationPathSet) {
        return filterDuplicateRelationPath(extractPath(newlyAddedRelations, allRelationPathSet));
    }

    private Set<BITableSourceRelationPath> extractPath(Set<BITableRelation> newlyAddedRelations, Set<BITableRelationPath> allRelationPathSet) {
        Set<BITableSourceRelationPath> sourcePathNeedBuild = new HashSet<BITableSourceRelationPath>();
        for (BITableRelationPath path : allRelationPathSet) {
            try {
                boolean containsRelation = false;
                for (BITableRelation relation : newlyAddedRelations) {
                    if (path.containsRelation(relation)) {
                        containsRelation = true;
                        break;
                    }
                }
                if (containsRelation) {
                    BITableSourceRelationPath relationPath = convertPath(path);
                    if (null != relationPath) {
                        sourcePathNeedBuild.add(relationPath);
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                continue;
            }
        }
        return sourcePathNeedBuild;
    }

    private Set<BITableSourceRelationPath> filterDuplicateRelationPath(Set<BITableSourceRelationPath> sourcePathNeedBuild) {
        logger.info("filter duplicate path");
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        Set<String> pathIDs = new HashSet<String>();
        for (BITableSourceRelationPath path : sourcePathNeedBuild) {
            try {
                String pathID = BIRelationIDUtils.calculatePathID(path);
                if (!pathIDs.contains(pathID)) {
                    pathIDs.add(pathID);
                    result.add(path);
                } else {
                    logger.info("The path id has present:\n" + BuildLogHelper.pathLogContent(path));
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return result;
    }

    protected Set<CubeTableSource> calculateSourceLayers(Set<List<Set<CubeTableSource>>> sourceLayers) {
        return set2Set(sourceLayers);
    }

    protected Set<List<Set<CubeTableSource>>> calculateSourceLayerDepends(Set<CubeTableSource> tableSourcesNeedBuild) {
        return calculateTableSource(tableSourcesNeedBuild);
    }

    private Set<BICubeGenerateRelation> calculateRelationDepends(Set<BITableSourceRelation> sourceRelationNeedBuild) {
        Set<BICubeGenerateRelation> relationDepends = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : sourceRelationNeedBuild) {
            BICubeGenerateRelation generateRelation = calculateDependTool.calRelations(biTableSourceRelation, this.getTableSources());
            checkRelationDepend(generateRelation);
            relationDepends.add(generateRelation);
        }
        return relationDepends;
    }

    private void checkRelationDepend(BICubeGenerateRelation generateRelation) {
        try {
            if (generateRelation.getDependTableSourceSet().size() == 1) {
                logger.warn("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend table: " + BuildLogHelper.tableLogContent("", generateRelation.getDependTableSourceSet().iterator().next()));
            } else if (generateRelation.getDependTableSourceSet().size() == 0) {
                logger.warn("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend none table ");
            } else if (generateRelation.getDependTableSourceSet().size() != 2) {
                StringBuffer sb = new StringBuffer("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend too many tables,the table size is: " + generateRelation.getDependTableSourceSet().size());
                sb.append("\n tables :");
                Iterator<CubeTableSource> it = generateRelation.getDependTableSourceSet().iterator();
                while (it.hasNext()) {
                    sb.append("\n").append(BuildLogHelper.tableLogContent(it.next()));
                }
                logger.error(sb.toString());
            }
        } catch (Exception e) {
            logger.warn("This is a specific error happened during recording log.It won't disturb main process.The error message:" + e.getMessage(), e);
        }
    }

    protected Set<CubeTableSource> extractTableSourceInRelation(Set<BITableSourceRelation> sourceRelationNeedBuild) {
        Set<CubeTableSource> tableSourcesInRelation = new HashSet<CubeTableSource>();
        Iterator<BITableSourceRelation> iterator = sourceRelationNeedBuild.iterator();
        while (iterator.hasNext()) {
            BITableSourceRelation relation = iterator.next();
            tableSourcesInRelation.add(relation.getForeignTable());
            tableSourcesInRelation.add(relation.getPrimaryTable());
        }
        return tableSourcesInRelation;
    }

    protected Set<CubeTableSource> extractTableSourceInNewlyTable(Set<BIBusinessTable> newlyAddedTables) {
        Set<CubeTableSource> tableSourcesNeedBuild = new HashSet<CubeTableSource>();
        for (BIBusinessTable biBusinessTable : newlyAddedTables) {
            tableSourcesNeedBuild.add(biBusinessTable.getTableSource());
        }
        return tableSourcesNeedBuild;
    }

    private Set<BITableSourceRelation> generateRelationNeedBuild(Set<BITableRelation> newlyAddedRelations) {
        return filterDuplicateRelation(extractTableSourceRelation(newlyAddedRelations));
    }

    private Set<BITableSourceRelation> extractTableSourceRelation(Set<BITableRelation> newlyAddedRelations) {
        Iterator<BITableRelation> iterator = newlyAddedRelations.iterator();
        Set<BITableSourceRelation> relationNeedBuild = new HashSet<BITableSourceRelation>();
        while (iterator.hasNext()) {
            BITableRelation relation = iterator.next();
            BITableSourceRelation sourceRelation = convertRelation(relation);
            if (null != sourceRelation) {
                relationNeedBuild.add(sourceRelation);
            }
        }
        return relationNeedBuild;
    }

    private void extractTablesInRelation() {
        Iterator<BITableRelation> iterator = newlyAddedRelations.iterator();
        while (iterator.hasNext()) {
            BITableRelation relation = iterator.next();
            BITableSourceRelation sourceRelation = convertRelation(relation);
            if (null != sourceRelation) {
                tableInConstruction.add(sourceRelation.getForeignTable());
                tableInConstruction.add(sourceRelation.getPrimaryTable());
                relationInConstruction.add(sourceRelation);
            }
        }
    }

    protected Set<BITableSourceRelation> filterDuplicateRelation(Set<BITableSourceRelation> relations) {
        logger.info("filter duplicate relation");
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        Set<String> relationID = new HashSet<String>();
        for (BITableSourceRelation relation : relations) {
            logger.info(BuildLogHelper.relationLogContent("", relation));
            String id = BIRelationIDUtils.calculateRelationID(relation);
            if (!relationID.contains(id)) {
                result.add(relation);
                relationID.add(id);
            } else {
                logger.info("The relation id has present:\n" + BuildLogHelper.relationLogContent("", relation));
            }

        }
        return result;
    }

    @Override
    public Set<BITableSourceRelationPath> getTableSourceRelationPathSet() {
        return pathInConstruction;
    }

    @Override
    public Set<CubeTableSource> getSingleSourceLayers() {
        return this.tableLayers;
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return this.relationInConstruction;
    }

    @Override
    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return this.tableSourceLayerDepends;
    }

    @Override
    public Set<BITableRelation> getTableRelationSet() {
        return this.newlyAddedRelations;
    }

    @Override
    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return pathDepends;
    }

    @Override
    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.relationDepends;
    }

    @Override
    public boolean copyFileFromOldCubes() {
        try {
            ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(Long.toString(userId));
            ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
            if (new File(tempConf.getRootURI().getPath()).exists()) {
                BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
            }
            if (new File(advancedConf.getRootURI().getPath()).exists()) {
                BIFileUtils.copyFolder(new File(advancedConf.getRootURI().getPath()), new File(tempConf.getRootURI().getPath()));
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return true;
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> updateSettingSourceMap = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : tableLayers) {
            updateSettingSourceMap.put(source, setUpdateTypes(source));
        }
        return updateSettingSourceMap;
    }

    @Override
    public String getCubeTaskId() {
        return DBConstant.GLOBAL_UPDATE_TYPE.PART_UPDATE;
    }
}
