package com.finebi.cube.impl.conf.helpers;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class DuplicateFilterHelper {

    private static BILogger LOGGER = BILoggerFactory.getLogger(DuplicateFilterHelper.class);

    public static void filterAll(Set<CubeTableSource> tableInConstruction,
                              Set<BITableSourceRelation> relationInConstruction,
                              Set<BITableSourceRelationPath> pathInConstruction) {
        filterPath(pathInConstruction);
        filterRelation(relationInConstruction);
        filterTable(tableInConstruction);
    }

    public static void filterTable(Set<CubeTableSource> tableInConstruction) {
        Set<CubeTableSource> result = filterDuplicateTable(tableInConstruction);
        tableInConstruction.clear();
        tableInConstruction.addAll(result);
    }

    public static void filterRelation(Set<BITableSourceRelation> relationInConstruction) {
        Set<BITableSourceRelation> result = filterDuplicateRelation(relationInConstruction);
        relationInConstruction.clear();
        relationInConstruction.addAll(result);
    }

    public static void filterPath(Set<BITableSourceRelationPath> pathInConstruction) {
        Set<BITableSourceRelationPath> result = filterDuplicateRelationPath(pathInConstruction);
        pathInConstruction.clear();
        pathInConstruction.addAll(result);
    }

    private static Set<CubeTableSource> filterDuplicateTable(Set<CubeTableSource> tableInConstruction) {
        LOGGER.info("filter duplicate table");
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Set<String> relationID = new HashSet<String>();
        for (CubeTableSource tableSource : tableInConstruction) {
            LOGGER.debug(BuildLogHelper.tableLogContent("", tableSource));
            String id = tableSource.getSourceID();
            if (!relationID.contains(id)) {
                result.add(tableSource);
                relationID.add(id);
            } else {
                LOGGER.info("The table source id has present:\n" + BuildLogHelper.tableLogContent("", tableSource));
            }

        }
        return result;
    }

    private static Set<BITableSourceRelation> filterDuplicateRelation(Set<BITableSourceRelation> relations) {
        LOGGER.info("filter duplicate relation");
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        Set<String> relationID = new HashSet<String>();
        for (BITableSourceRelation relation : relations) {
            LOGGER.debug(BuildLogHelper.relationLogContent("", relation));
            String id = BIRelationIDUtils.calculateRelationID(relation);
            if (!relationID.contains(id)) {
                result.add(relation);
                relationID.add(id);
            } else {
                LOGGER.info("The relation id has present:\n" + BuildLogHelper.relationLogContent("", relation));
            }

        }
        return result;
    }

    private static Set<BITableSourceRelationPath> filterDuplicateRelationPath(Set<BITableSourceRelationPath> pathInConstruction) {
        LOGGER.info("filter duplicate path");
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        Set<String> pathIDs = new HashSet<String>();
        for (BITableSourceRelationPath path : pathInConstruction) {
            try {
                String pathID = BIRelationIDUtils.calculatePathID(path);
                if (!pathIDs.contains(pathID)) {
                    pathIDs.add(pathID);
                    result.add(path);
                } else {
                    LOGGER.info("The path id has present:\n" + BuildLogHelper.pathLogContent(path));
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return result;
    }
}
