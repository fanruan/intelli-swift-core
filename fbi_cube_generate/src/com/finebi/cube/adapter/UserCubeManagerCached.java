package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.structure.ICube;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * This class created on 2016/6/8.
 *
 * @author Connery
 * @since 4.0
 */

@BIMandatedObject(module = IModuleFactory.CUBE_BASE_MODULE, factory = BIMateFactory.CUBE_BASE
        , implement = ICubeDataLoader.class)
public class UserCubeManagerCached implements ICubeDataLoader {
    private BIUserCubeManager hostCubeManager;
    private CubeTableCache cubeTableCache;

    public UserCubeManagerCached(long userID, ICube cube) {
        hostCubeManager = new BIUserCubeManager(userID, cube);
        cubeTableCache = new CubeTableCache(hostCubeManager);
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return cubeTableCache.getTableService(tableSource);
    }

    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return hostCubeManager.getFieldIndex(column);
    }

    @Override
    public long getUserId() {
        return hostCubeManager.getUserId();
    }

    @Override
    public boolean needReadTempValue() {
        return hostCubeManager.needReadTempValue();
    }

    @Override
    public boolean needReadCurrentValue() {
        return hostCubeManager.needReadCurrentValue();
    }

    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        return hostCubeManager.getNIOReaderManager();
    }

    @Override
    public void releaseCurrentThread() {
        hostCubeManager.releaseCurrentThread();
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end) {
        return cubeTableCache.getTableService(tableSource);
    }

    @Override
    public long getVersion() {
        return hostCubeManager.getVersion();
    }

    @Override
    public void clear() {
        hostCubeManager.clear();
    }
}
