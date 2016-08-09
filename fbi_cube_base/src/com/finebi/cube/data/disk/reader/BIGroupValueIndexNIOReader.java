package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexCreator;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexNIOReader implements ICubeGroupValueIndexReader {

    private static final int MAX_CACHE_SIZE = 1024;


    protected ICubeByteArrayReader byteArray;

    private LRUWithKHashMap<Integer, GroupValueIndex> cache;

    public BIGroupValueIndexNIOReader(ICubeByteArrayReader byteList) {
        this.byteArray = byteList;
        cache = new LRUWithKHashMap<Integer, GroupValueIndex>((MAX_CACHE_SIZE >> 2));
    }

    @Override
    public GroupValueIndex getSpecificValue(final int rowNumber) throws BIResourceInvalidException {
        return cache.get(rowNumber, new ValueCreator<GroupValueIndex>() {

            @Override
            public GroupValueIndex createNewObject() {
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
            cache.clear();
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
