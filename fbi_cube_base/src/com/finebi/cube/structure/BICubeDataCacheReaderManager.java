package com.finebi.cube.structure;

import com.finebi.cube.ICubeEntityDataReaderManagerService;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDataCacheReaderManager extends BIMapContainer<BIKey, ICubePrimitiveReader> implements ICubeEntityDataReaderManagerService {

    @Override
    protected Map<BIKey, ICubePrimitiveReader> initContainer() {
        return new ConcurrentHashMap<BIKey, ICubePrimitiveReader>();
    }

    @Override
    protected ICubePrimitiveReader generateAbsentValue(BIKey key) {
        return null;
    }

    @Override
    public ICubePrimitiveReader getCubeReader(BIKey readerKey) throws BIKeyAbsentException {
        return getValue(readerKey);

    }

    @Override
    public void registerCubeReader(BIKey readerKey, ICubePrimitiveReader cubeReader) throws BIKeyDuplicateException {
        putKeyValue(readerKey, cubeReader);
    }

    @Override
    public void removeCubeReader(BIKey readerKey) throws BIKeyAbsentException {
        remove(readerKey);
    }
}
