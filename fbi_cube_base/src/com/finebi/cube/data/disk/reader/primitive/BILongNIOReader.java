package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.general.ComparatorUtils;

import java.io.File;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BILongNIOReader extends BIBasicNIOReader<Long> implements ICubeLongReader {
    private Map<Long, LongBuffer> longBuffers = new ConcurrentHashMap<Long, LongBuffer>();

    public BILongNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BILongNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }

    @Override
    protected Long getValue(Long page, int index) throws BIResourceInvalidException {
        if(isValid) {
            try {
                long value = longBuffers.get(page).get(index);
                return value == Long.MIN_VALUE ? null : value;
            } finally {
            }
        } else {
            throw new BIResourceInvalidException();
        }
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            Iterator<Entry<Long, LongBuffer>> iter = longBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, LongBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            longBuffers.put(index, buffer.asLongBuffer());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.LONG.PAGE_STEP;
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.LONG.PAGE_MODE_TO_AND_READ_VALUE;
    }
}