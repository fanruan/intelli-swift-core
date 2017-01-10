package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;

/**
 * Created by Connery on 10/27/2016.
 */
public class BuildLogHelper {
    private static BILogger logger = BILoggerFactory.getLogger(BuildLogHelper.class);

    public static String calculateRelationID(BITableSourceRelation relation) {
        return BIRelationIDUtils.calculateRelationID(relation);
    }

    public static String calculateRelationID(BICubeRelation cubeRelation) {
        return BIRelationIDUtils.calculateRelationID(cubeRelation);
    }

    public static String calculatePathID(BITableSourceRelationPath path) {
        return BIRelationIDUtils.calculatePathID(path);
    }

    public static String calculatePathID(BICubeTablePath cubeTablePath) {
        return BIRelationIDUtils.calculatePathID(cubeTablePath);
    }

    public static String pathLogContent(String prefix, BITableSourceRelationPath path) {
        try {
            Integer countRelation = 0;
            StringBuffer sb = new StringBuffer();
            sb.append("Path ID:").append(calculatePathID(path)).append("\nRelation List:");
            for (BITableSourceRelation relation : path.getAllRelations()) {
                sb.append(BIStringUtils.append(
                        "\nRelation " + (countRelation++) + ":",
                        relationLogContent(prefix + "     ", relation)));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "path error";
        }
    }

    public static String pathLogContent(BITableSourceRelationPath path) {
        return pathLogContent("", path);
    }

    public static String relationLogContent(String prefix, BITableSourceRelation relation) {
        try {
            return BIStringUtils.append(
                    "\n" + prefix + "       Relation ID:", calculateRelationID(relation),
                    "\n" + prefix + "       primaryTable:", relation.getPrimaryTable().getTableName() + "," + relation.getPrimaryTable().getSourceID(),
                    "\n" + prefix + "       foreignTable:", relation.getForeignTable().getTableName() + "," + relation.getForeignTable().getSourceID(),
                    "\n" + prefix + "       primaryField:", relation.getPrimaryField().getFieldName(),
                    "\n" + prefix + "       foreignField:", relation.getForeignField().getFieldName());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "relation error";
        }
    }

    public static String relationLogContent(BITableSourceRelation relation) {
        return relationLogContent("", relation);
    }

    public static String tableLogContent(String prefix, CubeTableSource tableSource) {
        try {
            return BIStringUtils.append(
                    "\n" + prefix + "       Table Name:", tableSource.getTableName(),
                    "\n" + prefix + "       Table ID:", tableSource.getSourceID()
            );
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "table source error";
        }
    }

    public static String tableLogContent(CubeTableSource tableSource) {
        return tableLogContent("", tableSource);
    }
}
