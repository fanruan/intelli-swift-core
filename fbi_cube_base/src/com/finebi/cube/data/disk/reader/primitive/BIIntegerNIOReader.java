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

public class BIIntegerNIOReader extends BIBasicNIOReader implements ICubeIntegerReader {
    private Map<Integer, IntBuffer> intBuffers = new ConcurrentHashMap<Integer, IntBuffer>();
    private IntBuffer[] intBufferArray = new IntBuffer[1];

    public BIIntegerNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public int getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return intBufferArray[getPage(filePosition)].get(getIndex(filePosition));
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the expect page value is:" + e);
        } finally {
        }

    }

    public BIIntegerNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {

            Iterator<Entry<Integer, IntBuffer>> iter = intBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, IntBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
            intBufferArray = new IntBuffer[1];
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected void initChild(int index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            intBuffers.put(index, buffer.asIntBuffer());
            if (intBufferArray.length < index){
                IntBuffer[] temp = new IntBuffer[index];
                System.arraycopy(intBufferArray, 0, temp, 0, intBufferArray.length);
                intBufferArray = temp;
            }
            intBufferArray[index] = intBuffers.get(index);
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