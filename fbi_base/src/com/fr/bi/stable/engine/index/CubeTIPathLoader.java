package com.fr.bi.stable.engine.index;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.source.SourceFile;

/**
 * Created by 小灰灰 on 2015/12/16.
 */
public interface CubeTIPathLoader extends Release {
    /**
     * 根据表的存储路径获取BITableIndex
     *
     * @param
     * @return
     */
    ICubeTableService getTableIndexByPath(SourceFile file);

    void releaseCurrentThread();

}