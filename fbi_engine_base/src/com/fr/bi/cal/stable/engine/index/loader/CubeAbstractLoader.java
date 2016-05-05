package com.fr.bi.cal.stable.engine.index.loader;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;


public abstract class CubeAbstractLoader implements ICubeDataLoader {

    /**
     *
     */
    private static final long serialVersionUID = 615702324431018412L;

    private static final int CACHE_SIZE = 128;
    protected BIUser biUser;
    protected LRUWithKHashMap<BICore, ICubeTableService> indexMap = new LRUWithKHashMap<BICore, ICubeTableService>(CACHE_SIZE);

    public CubeAbstractLoader(long userId) {
        biUser = new BIUser(userId);
    }

    /**
     * 释放
     */
    @Override
    public void releaseResource() {
        synchronized (this) {
        	if(indexMap != null){
        		indexMap.releaseResource();
        	}
        	if(getNIOReaderManager() != null){
        		getNIOReaderManager().releaseResource();
        	}
        }
    }


    @Override
    public BIKey getFieldIndex(BIField column) {
        ICubeTableService ti = getTableIndex(column);
        return ti.getColumnIndex(column);
    }

    @Override
    public void releaseCurrentThread() {

    }

    @Override
    public ICubeTableService getTableIndex(BICore core, int start, int end) {
        return getTableIndex(core);
    }

}