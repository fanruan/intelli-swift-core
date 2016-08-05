package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.manager.PlugManager;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexCreator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexNIOReader implements ICubeGroupValueIndexReader {

    protected ICubeByteArrayReader byteArray;

    private LoadingCache<Integer, GroupValueIndex> cache;

    public BIGroupValueIndexNIOReader(ICubeByteArrayReader byteList) {
        this.byteArray = byteList;
        cache = CacheBuilder.newBuilder()
                .weigher(new Weigher<Integer, GroupValueIndex>() {
                    @Override
                    public int weigh(Integer integer, GroupValueIndex groupValueIndex) {
                        return groupValueIndex.getRowsCountWithData();
                    }
                })
                .maximumWeight(PlugManager.getPerformancePlugManager().getMaxGVICacheCount())
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, GroupValueIndex>() {
                    @Override
                    public GroupValueIndex load(Integer rowNumber) throws Exception {
                        byte[] b = new byte[0];
                        try {
                            b = byteArray.getSpecificValue(rowNumber);
                        } catch (BIResourceInvalidException e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        }
                        return GroupValueIndexCreator.createGroupValueIndex(b);
                    }
                });
    }


    @Override
    public GroupValueIndex getSpecificValue(final int rowNumber) throws BIResourceInvalidException {
        try {
            return cache.get(rowNumber);
        } catch (ExecutionException e) {
            BINonValueUtils.beyondControl(e);
        }
        throw new BIResourceInvalidException();
    }

    @Override
    public long getLastPosition(long rowCount) {
        return byteArray.getLastPosition(rowCount);
    }

    @Override
    public void clear() {

        if (byteArray != null) {
            byteArray.clear();
            byteArray = null;
        }
        if (cache != null) {
            cache.invalidateAll();
        }

    }

    @Override
    public boolean canRead() {
        return byteArray.canRead();
    }

    @Override
    public void forceRelease() {
        byteArray.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return byteArray.isForceReleased();
    }
}
