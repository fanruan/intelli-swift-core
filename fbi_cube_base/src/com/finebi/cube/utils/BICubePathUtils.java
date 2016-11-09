package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.exception.BITablePathEmptyException;
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
    private static BILogger logger = BILoggerFactory.getLogger(BICubePathUtils.class);

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

    public static boolean isPathExisted(BITableSourceRelationPath sourcePath, ICubeConfiguration cubeConfiguration) {
        BICube cube = new BICube(new BICubeResourceRetrieval(cubeConfiguration), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        BICubeTablePath path = convert(sourcePath);
        ITableKey tableKey = null;
        try {
            tableKey = path.getFirstRelation().getPrimaryTable();
        } catch (BITablePathEmptyException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
        return cube.exist(tableKey, path);
    }
}
