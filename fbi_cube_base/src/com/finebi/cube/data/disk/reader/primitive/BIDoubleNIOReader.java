package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeDoubleReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BIDoubleNIOReader extends BIBasicNIOReader implements ICubeDoubleReader {

    private Map<Integer, DoubleBuffer> doubleBuffers = new ConcurrentHashMap<Integer, DoubleBuffer>();

    private transient DoubleBuffer[] doubleBufferArray = new DoubleBuffer[1];

    public BIDoubleNIOReader(File cacheFile) {
        super(cacheFile);
    }


    public BIDoubleNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }


    public double getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return doubleBufferArray[getPage(filePosition)].get(getIndex(filePosition));
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the expect page value is:", e);
        } finally {
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
            Iterator<Entry<Integer, DoubleBuffer>> iter = doubleBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, DoubleBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
            doubleBufferArray = new DoubleBuffer[1];
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.DOUBLE.PAGE_MODE_TO_AND_READ_VALUE;
    }

    @Override
    protected void initChild(int index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            doubleBuffers.put(index, buffer.asDoubleBuffer());
            if (doubleBufferArray.length < index){
                DoubleBuffer[] temp = new DoubleBuffer[index];
                System.arraycopy(doubleBufferArray, 0, temp, 0, doubleBufferArray.length);
                doubleBufferArray = temp;
            }
            doubleBufferArray[index] = doubleBuffers.get(index);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}