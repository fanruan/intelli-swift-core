package com.finebi.cube.tools;

import com.finebi.cube.structure.BICubeTablePath;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePathTestTool {
    public static BICubeTablePath getABC() {
        try {
            BICubeTablePath path = new BICubeTablePath();
            path.addRelationAtHead(BICubeRelationTestTool.getTaTb());
            path.addRelationAtTail(BICubeRelationTestTool.getTbTc());
            return path;
        } catch (BITablePathConfusionException e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl();
        }
    }

    public static BICubeTablePath getContainNullPath() {
        try {
            BICubeTablePath path = new BICubeTablePath();
            path.addRelationAtHead(BICubeRelationTestTool.getNullTableRelation());
            return path;
        } catch (BITablePathConfusionException e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl();
        }
    }
}
