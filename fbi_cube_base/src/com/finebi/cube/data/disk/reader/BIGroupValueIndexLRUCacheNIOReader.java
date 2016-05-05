package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexCreator;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by naleite on 16/3/16.
 */
public class BIGroupValueIndexLRUCacheNIOReader extends BIGroupValueIndexNIOReader {

    private static final int MAX_CACHE_SIZE = 1024;

    private LRUWithKHashMap<Integer, GroupValueIndex> cache;

    public BIGroupValueIndexLRUCacheNIOReader(ICubeByteArrayReader byteList) {
        super(byteList);
        cache = new LRUWithKHashMap<Integer, GroupValueIndex>((int) (MAX_CACHE_SIZE >> 2));
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
                GroupValueIndex result = GroupValueIndexCreator.createGroupValueIndex(b);
                return result;
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        if (cache != null) {
            cache.clear();
        }
    }
}
