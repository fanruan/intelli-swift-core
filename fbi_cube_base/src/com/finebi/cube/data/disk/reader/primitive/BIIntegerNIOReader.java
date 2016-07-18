package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BIIntegerNIOReader extends BIBasicNIOReader<Integer> implements ICubeIntegerReader {
    private Map<Long, IntBuffer> intBuffers = new ConcurrentHashMap<Long, IntBuffer>();


    public BIIntegerNIOReader(File cacheFile) {
        super(cacheFile);
    }

    @Override
    protected Integer getValue(Long page, int index) throws  BIResourceInvalidException{
        if(isValid) {
            try {
                int result = intBuffers.get(page).get(index);
                return result == Integer.MIN_VALUE ? null : result;
            } finally {
            }
        } else {
            throw new BIResourceInvalidException();
        }
    }

    public BIIntegerNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {

            Iterator<Entry<Long, IntBuffer>> iter = intBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, IntBuffer> entry = iter.next();
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
            intBuffers.put(index, buffer.asIntBuffer());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.INTEGER.PAGE_STEP;
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.INTEGER.PAGE_MODE_TO_AND_READ_VALUE;
    }
}