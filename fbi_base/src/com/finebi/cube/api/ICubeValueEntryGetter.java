package com.finebi.cube.api;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.object.CubeValueEntry;

/**
 * Created by 小灰灰 on 2016/7/5.
 */
public interface ICubeValueEntryGetter {
    /**
     * 根据行号获取该行对应的索引
     * @param row
     * @return
     */
    GroupValueIndex getIndexByRow(int row);

    /**
     * 根据行号获取该行对应的CubeValueEntry
     * @param row
     * @return
     */
    CubeValueEntry getEntryByRow(int row);

    /**
     * 根据行号获取在分组中的位置
     * @param row
     * @return
     */
    Integer getPositionOfGroupByRow(int row);

    int getGroupSize();
}
