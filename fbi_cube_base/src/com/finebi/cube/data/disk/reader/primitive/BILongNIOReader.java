package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BILongNIOReader extends BIBasicNIOReader implements ICubeLongReader {
    private Map<Integer, LongBuffer> longBuffers = new ConcurrentHashMap<Integer, LongBuffer>();
    private transient LongBuffer[] longBufferArray = new LongBuffer[1];


    public BILongNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BILongNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }


    public long getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return longBufferArray[getPage(filePosition)].get(getIndex(filePosition));
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the expect page value is:" + e);
        } finally {
        }

    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            Iterator<Entry<Integer, LongBuffer>> iter = longBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, LongBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
            longBufferArray = new LongBuffer[1];
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected void initChild(int index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            longBuffers.put(index, buffer.asLongBuffer());
            if (longBufferArray.length < index){
                LongBuffer[] temp = new LongBuffer[index];
                System.arraycopy(longBufferArray, 0, temp, 0, longBufferArray.length);
                longBufferArray = temp;
            }
            longBufferArray[index] = longBuffers.get(index);
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