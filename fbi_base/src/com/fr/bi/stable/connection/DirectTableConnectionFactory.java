package com.fr.bi.stable.connection;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.List;

/**
 * Created by GUY on 2015/3/26.
 */
public class DirectTableConnectionFactory {

    public static ConnectionRowGetter createConnectionRow(List<BITableSourceRelation> relationList, ICubeDataLoader loader) {
        ICubeTableIndexReader reader = createDirectTableConnection(relationList, loader);
        return new ConnectionRowGetter(reader);
    }

    /**
     * 创建直接表链接
     *
     * @param loader CubeTILoader对象
     * @return DirectTableConnection对象
     */
    private static ICubeTableIndexReader createDirectTableConnection(List<BITableSourceRelation> relationList, ICubeDataLoader loader) {
        if (relationList == null){
            throw BINonValueUtils.beyondControl();
        } else if (relationList.isEmpty()){
            return null;
        } else {
            return loader.getTableIndex(relationList.get(0).getPrimaryTable()).ensureBasicIndex(relationList);
        }
    }
}