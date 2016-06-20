package com.finebi.cube.tools;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelationPathTestTool {
    public static BITableSourceRelationPath generatePathABC() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getAB()).addRelationAtTail(BITableSourceRelationTestTool.getBC());
            return path;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public static BITableSourceRelationPath generatePathBC() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getBC());
            return path;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public static BITableSourceRelationPath generatePathAaBC() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getAaB()).addRelationAtTail(BITableSourceRelationTestTool.getBC());
            return path;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }


    public static List<BITableSourceRelation> getABCList() {
        try {
            List<BITableSourceRelation> path = new ArrayList<BITableSourceRelation>();
            CubeTableSource A = BIMemoryDataSourceFactory.generateTableA();
            CubeTableSource B = BIMemoryDataSourceFactory.generateTableB();
            CubeTableSource C = BIMemoryDataSourceFactory.generateTableC();
            BITableSourceRelation a2b = new BITableSourceRelation(A.getFieldsArray(null)[1], B.getFieldsArray(null)[2],
                    A, B);
            BITableSourceRelation b2c = new BITableSourceRelation(B.getFieldsArray(null)[1], C.getFieldsArray(null)[2],
                    B, C);
            path.add(a2b);
            path.add(b2c);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public static BITableSourceRelationPath getABPath() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryAB());
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public static BITableSourceRelationPath getABCPath() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryAB());
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryBC());
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public static Set<BITableSourceRelationPath> getRelationPathSetABC() {
        Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
        pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
        return pathSet;
    }

    public static BITableSourceRelationPath getABCDPath() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryAB());
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryBC());
            path.addRelationAtTail(BITableSourceRelationTestTool.getMemoryCD());
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
