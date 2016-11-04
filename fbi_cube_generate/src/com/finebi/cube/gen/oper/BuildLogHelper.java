package com.finebi.cube.gen.oper;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Connery on 10/27/2016.
 */
public class BuildLogHelper {


    public static String calculateRelationID(BITableSourceRelation relation) {
        return BIRelationIDUtils.calculateRelationID(relation);
    }

    public static String calculateRelationID(BICubeRelation cubeRelation) {
       return  BIRelationIDUtils.calculateRelationID(cubeRelation);
    }

    public static String calculatePathID(BITableSourceRelationPath path) {
return BIRelationIDUtils.calculatePathID(path);
    }

    public static String calculatePathID(BICubeTablePath cubeTablePath) {
        return BIRelationIDUtils.calculatePathID(cubeTablePath);
    }

}
