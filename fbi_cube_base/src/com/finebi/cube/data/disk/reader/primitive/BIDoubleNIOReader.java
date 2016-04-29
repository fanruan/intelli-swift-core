package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeDoubleReader;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BIDoubleNIOReader extends BIBasicNIOReader<Double> implements ICubeDoubleReader {

    private Map<Long, DoubleBuffer> doubleBuffers = new ConcurrentHashMap<Long, DoubleBuffer>();

    public BIDoubleNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BIDoubleNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }

    @Override
    protected Double getValue(Long page, int index) {
        readWriteLock.readLock().lock();
        try {
            Double result = doubleBuffers.get(page).get(index);
            return !result.equals(Double.valueOf(Double.NaN)) ? result : null;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    @Override
    protected long getPageStep() {
        return NIOConstant.DOUBLE.PAGE_STEP;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            Iterator<Entry<Long, DoubleBuffer>> iter = doubleBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, DoubleBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.DOUBLE.PAGE_MODE_TO_AND_READ_VALUE;
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            doubleBuffers.put(index, buffer.asDoubleBuffer());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}