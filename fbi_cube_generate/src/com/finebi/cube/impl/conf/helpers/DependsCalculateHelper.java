package com.finebi.cube.impl.conf.helpers;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.BIDataStructTranUtils;
import com.fr.bi.conf.data.source.BIOccupiedCubeTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class DependsCalculateHelper {

    private static BILogger LOGGER = BILoggerFactory.getLogger(DependsCalculateHelper.class);

    public static Set<List<Set<CubeTableSource>>> calculateSourceLayerDepends(Set<CubeTableSource> tableSourcesNeedBuild) {
        return calculateTableSource(tableSourcesNeedBuild);
    }

    public static Set<BICubeGenerateRelationPath> calculatePathDepends(CalculateDependTool calculateDependTool, Set<BITableSourceRelationPath> pathInConstruction, Set<BITableSourceRelation> relationInConstruction) {
        return calculateDependTool.calRelationPath(pathInConstruction, relationInConstruction);
    }

    public static Set<BICubeGenerateRelation> calculateRelationDepends(CalculateDependTool calculateDependTool, Set<BITableSourceRelation> relationInConstruction, Set<CubeTableSource> tableInConsturction) {
        Set<BICubeGenerateRelation> relationDepends = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : relationInConstruction) {
            BICubeGenerateRelation generateRelation = calculateDependTool.calRelations(biTableSourceRelation, tableInConsturction);
            checkRelationDepend(generateRelation);
            relationDepends.add(generateRelation);
        }
        return relationDepends;
    }

    public static Set<List<Set<CubeTableSource>>> calculateTableSource(Set<CubeTableSource> tableSources) {
        Iterator<CubeTableSource> it = tableSources.iterator();
        Set<List<Set<CubeTableSource>>> depends = new HashSet<List<Set<CubeTableSource>>>();
        while (it.hasNext()) {
            CubeTableSource tableSource = it.next();
            if (tableSource instanceof BIOccupiedCubeTableSource) {
                List<Set<CubeTableSource>> dependList = tableSource.createGenerateTablesList();
                Iterator setIt = dependList.iterator();
                while (setIt.hasNext()) {
                    Set<CubeTableSource> set = (Set<CubeTableSource>) setIt.next();
                    Iterator cubeTableSourceIt = set.iterator();
                    while (cubeTableSourceIt.hasNext()) {
                        CubeTableSource cubeTableSource = (CubeTableSource) cubeTableSourceIt.next();
                        if (!tableSources.contains(cubeTableSource)) {
                            cubeTableSourceIt.remove();
                        }
                    }
                }
                depends.add(dependList);
            } else {
                depends.add(tableSource.createGenerateTablesList());
            }
        }
        return depends;
    }

    public static Set<CubeTableSource> calculateSourceLayers(Set<List<Set<CubeTableSource>>> sourceLayers) {
        return BIDataStructTranUtils.set2Set(sourceLayers);
    }

    private static void checkRelationDepend(BICubeGenerateRelation generateRelation) {
        try {
            if (generateRelation.getDependTableSourceSet().size() == 1) {
                LOGGER.warn("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend table: " + BuildLogHelper.tableLogContent("", generateRelation.getDependTableSourceSet().iterator().next()));
            } else if (generateRelation.getDependTableSourceSet().size() == 0) {
                LOGGER.warn("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend none table ");
            } else if (generateRelation.getDependTableSourceSet().size() != 2) {
                StringBuffer sb = new StringBuffer("the relation should depend two table in increasing build mode:" + BuildLogHelper.relationLogContent("", generateRelation.getRelation()) +
                        "\n the depend too many tables,the table size is: " + generateRelation.getDependTableSourceSet().size());
                sb.append("\n tables :");
                Iterator<CubeTableSource> it = generateRelation.getDependTableSourceSet().iterator();
                while (it.hasNext()) {
                    sb.append("\n").append(BuildLogHelper.tableLogContent(it.next()));
                }
                LOGGER.error(sb.toString());
            }
        } catch (Exception e) {
            LOGGER.warn("This is a specific error happened during recording log.It won't disturb main process.The error message:" + e.getMessage(), e);
        }
    }

    /**
     * depends中的基础表如果不在本次生成的集合中，则剔除。
     *
     * @param tableSourceLayerDepends
     * @param basicSourceIds
     */
    public static void removeBasicTableInDepends(Set<List<Set<CubeTableSource>>> tableSourceLayerDepends, Set<String> basicSourceIds) {
        for (List<Set<CubeTableSource>> tableSourceLayerDependList : tableSourceLayerDepends) {
            for (Set<CubeTableSource> currentTableSources : tableSourceLayerDependList) {
                Iterator<CubeTableSource> iterator = currentTableSources.iterator();
                while (iterator.hasNext()) {
                    CubeTableSource tableSource = iterator.next();
                    if (TableSourceUtils.isBasicTable(tableSource)) {
                        if (!basicSourceIds.contains(tableSource.getSourceID())) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }
}
