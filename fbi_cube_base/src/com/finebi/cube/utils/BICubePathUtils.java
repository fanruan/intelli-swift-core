package com.finebi.cube.utils;

import com.finebi.cube.structure.BICubeTablePath;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePathUtils {
    public static BICubeTablePath convert(BITableSourceRelationPath targetPath) {
        BITableSourceRelationPath path = new BITableSourceRelationPath();
        path.copyFrom(targetPath);
        BICubeTablePath cubePath = new BICubeTablePath();
        try {
            while (!path.isEmptyPath()) {
                cubePath.addRelationAtHead(BICubeRelationUtils.convert(path.getLastRelation()));
                path.removeLastRelation();
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return cubePath;
    }

    public static BICubeTablePath convert(List<BITableSourceRelation> targetPath) {
        BICubeTablePath cubePath = new BICubeTablePath();
        Iterator<BITableSourceRelation> it = targetPath.iterator();
        try {
            while (it.hasNext()) {
                /**
                 * 从尾部添加
                 */
                cubePath.addRelationAtTail(BICubeRelationUtils.convert(it.next()));
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return cubePath;
    }


}
