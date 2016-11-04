package com.finebi.cube.gen.oper;

import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BICubeRelationUtils;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Connery on 10/27/2016.
 */
public class BuildLogHelper {
    private static final Logger logger = LoggerFactory.getLogger(BuildLogHelper.class);

    public static String calculateRelationID(BITableSourceRelation relation) {
        BICubeRelation cubeRelation = BICubeRelationUtils.convert(relation);
        return calculateRelationID(cubeRelation);
    }

    public static String calculateRelationID(BICubeRelation cubeRelation) {
        ITableKey tableKey = cubeRelation.getPrimaryTable();
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(cubeRelation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(cubeRelation.toString() + " the relation is so terrible");
        }
        return calculatePathID(tableKey, relationPath);
    }

    public static String calculatePathID(BITableSourceRelationPath path) {

        BICubeTablePath cubeTablePath = BICubePathUtils.convert(path);
        return calculatePathID(cubeTablePath);
    }

    public static String calculatePathID(BICubeTablePath cubeTablePath) {

        ITableKey tableKey = null;
        try {
            tableKey = cubeTablePath.getFirstRelation().getPrimaryTable();
        } catch (BITablePathEmptyException e) {
            logger.error(e.getMessage(), e);
        }
        return calculatePathID(tableKey, cubeTablePath);
    }

    public static String calculatePathID(ITableKey tableKey, BICubeTablePath relationPath) {
        return BICubeResourceRetrieval.calculateTableRelationSourceID(tableKey, relationPath);
    }
}
