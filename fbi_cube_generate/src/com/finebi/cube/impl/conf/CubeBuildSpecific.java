package com.finebi.cube.impl.conf;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.AbstractCubeBuildStuff;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.relation.*;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/11/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class CubeBuildSpecific extends AbstractCubeBuildStuff implements CubeBuildStuff {
    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildSpecific.class);

    protected Set<CubeTableSource> tableLayers;
    protected Set<CubeTableSource> tableInConstruction;
    protected Set<BITableSourceRelation> relationInConstruction;
    protected Set<BICubeGenerateRelation> relationDepends;
    protected Set<List<Set<CubeTableSource>>> tableSourceLayerDepends;
    protected Set<BITableSourceRelationPath> pathInConstruction;
    protected Set<BICubeGenerateRelationPath> pathDepends;

    public CubeBuildSpecific(long userId) {
        super(userId);
    }

    protected Set<BITableSourceRelation> calculateRelevantRelation(Set<CubeTableSource> tableInConstruction) {
        Set<String> tableID = new HashSet<String>();
        Set<BITableSourceRelation> relationsAboutTable = new HashSet<BITableSourceRelation>();
        for (CubeTableSource tableSource : tableInConstruction) {
            tableID.add(tableSource.getSourceID());
        }
        for (BITableRelation relation : configHelper.getSystemTableRelations()) {
            if (tableID.contains(relation.getPrimaryTable().getTableSource().getSourceID())) {
                relationsAboutTable.add(configHelper.convertRelation(relation));
            } else if (tableID.contains(relation.getForeignTable().getTableSource().getSourceID())) {
                relationsAboutTable.add(configHelper.convertRelation(relation));
            }
        }
        return relationsAboutTable;
    }

    protected Set<BITableSourceRelationPath> calculateRelevantPath(Set<BITableSourceRelation> relationInConstruction) {
        Set<String> relationIDs = new HashSet<String>();
        Set<BITableSourceRelationPath> pathsAboutRelation = new HashSet<BITableSourceRelationPath>();
        for (BITableSourceRelation relation : relationInConstruction) {
            relationIDs.add(BIRelationIDUtils.calculateRelationID(relation));
        }
        for (BITableRelationPath path : configHelper.getSystemTablePaths()) {
            /**
             * 等于1的path，实际就是relation了。这部分在relation地方处理了。
             */
            if (path.size() >= 2) {
                BITableSourceRelationPath sourceRelationPath = configHelper.convertPath(path);
                for (BITableSourceRelation relation : sourceRelationPath.getAllRelations()) {
                    if (relationIDs.contains(BIRelationIDUtils.calculateRelationID(relation))) {
                        pathsAboutRelation.add(sourceRelationPath);
                        break;
                    }
                }
            }

        }
        return pathsAboutRelation;
    }

    protected void filter() {
        filterPath();
        filterRelation();
        filterTable();
    }

    protected void calculateRelevantStuff(Set<CubeTableSource> tableInConstruction) {
        this.relationInConstruction.addAll(calculateRelevantRelation(tableInConstruction));
        this.pathInConstruction.addAll(calculateRelevantPath(this.relationInConstruction));
    }

    protected void filterTable() {
        Set<CubeTableSource> result = filterDuplicateTable(this.tableInConstruction);
        this.tableInConstruction.clear();
        this.tableInConstruction.addAll(result);
    }

    protected void filterRelation() {
        Set<BITableSourceRelation> result = filterDuplicateRelation(this.relationInConstruction);
        this.relationInConstruction.clear();
        this.relationInConstruction.addAll(result);
    }

    protected void filterPath() {
        Set<BITableSourceRelationPath> result = filterDuplicateRelationPath(this.pathInConstruction);
        this.pathInConstruction.clear();
        this.pathInConstruction.addAll(result);
    }

    protected Set<CubeTableSource> filterDuplicateTable(Set<CubeTableSource> tableInConstruction) {
        logger.info("filter duplicate table");
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Set<String> relationID = new HashSet<String>();
        for (CubeTableSource tableSource : tableInConstruction) {
            logger.debug(BuildLogHelper.tableLogContent("", tableSource));
            String id = tableSource.getSourceID();
            if (!relationID.contains(id)) {
                result.add(tableSource);
                relationID.add(id);
            } else {
                logger.info("The table source id has present:\n" + BuildLogHelper.tableLogContent("", tableSource));
            }

        }
        return result;
    }

    protected Set<BITableSourceRelation> filterDuplicateRelation(Set<BITableSourceRelation> relations) {
        logger.info("filter duplicate relation");
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        Set<String> relationID = new HashSet<String>();
        for (BITableSourceRelation relation : relations) {
            logger.debug(BuildLogHelper.relationLogContent("", relation));
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

    protected void calculateDepends(Set<CubeTableSource> tableInConstruction, Set<BITableSourceRelation> relationInConstruction, Set<BITableSourceRelationPath> pathInConstruction) {
        this.tableSourceLayerDepends = calculateSourceLayerDepends(tableInConstruction);
        this.tableLayers = calculateSourceLayers(tableSourceLayerDepends);
        this.relationDepends = calculateRelationDepends(relationInConstruction, tableInConstruction);
        this.pathDepends = calculatePathDepends(pathInConstruction, relationInConstruction);
    }



    protected Set<BICubeGenerateRelationPath> calculatePathDepends(Set<BITableSourceRelationPath> pathInConstruction, Set<BITableSourceRelation> relationInConstruction) {
        return calculateDependTool.calRelationPath(pathInConstruction, relationInConstruction);
    }


    protected Set<CubeTableSource> calculateSourceLayers(Set<List<Set<CubeTableSource>>> sourceLayers) {
        return set2Set(sourceLayers);
    }

    protected Set<List<Set<CubeTableSource>>> calculateSourceLayerDepends(Set<CubeTableSource> tableSourcesNeedBuild) {
        return calculateTableSource(tableSourcesNeedBuild);
    }

    protected void checkRelationDepend(BICubeGenerateRelation generateRelation) {
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

    protected Set<BICubeGenerateRelation> calculateRelationDepends(Set<BITableSourceRelation> relationInConstruction, Set<CubeTableSource> tableInConsturction) {
        Set<BICubeGenerateRelation> relationDepends = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : relationInConstruction) {
            BICubeGenerateRelation generateRelation = calculateDependTool.calRelations(biTableSourceRelation, tableInConsturction);
            checkRelationDepend(generateRelation);
            relationDepends.add(generateRelation);
        }
        return relationDepends;
    }

    private Set<BITableSourceRelationPath> filterDuplicateRelationPath(Set<BITableSourceRelationPath> pathInConstruction) {
        logger.info("filter duplicate path");
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        Set<String> pathIDs = new HashSet<String>();
        for (BITableSourceRelationPath path : pathInConstruction) {
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
    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return pathDepends;
    }

    @Override
    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.relationDepends;
    }
}
