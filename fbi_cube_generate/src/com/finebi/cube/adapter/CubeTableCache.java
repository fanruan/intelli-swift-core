package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2016/6/8.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeTableCache {
    private LoadingCache<CubeTableSource, ICubeTableService> cache;
    private BIUserCubeManager userCubeManager;

    public CubeTableCache(BIUserCubeManager biUserCubeManager) {
        userCubeManager = biUserCubeManager;
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<CubeTableSource, ICubeTableService>() {
                    @Override
                    public ICubeTableService load(CubeTableSource tableSource) throws Exception {
                        return userCubeManager.getTableIndex(tableSource);
                    }
                });
    }

    public ICubeTableService getTableService(CubeTableSource tableSource) {
        try {
            return cache.get(tableSource);
        } catch (ExecutionException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

}
