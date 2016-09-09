package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BIByteNIOReader extends BIBasicNIOReader implements ICubeByteReader {

    private Map<Integer, ByteBuffer> byteBuffers = new ConcurrentHashMap<Integer, ByteBuffer>();
    private ByteBuffer[] byteBufferArray = new ByteBuffer[1];

    public BIByteNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BIByteNIOReader(String cacheFilePath) {
        super(cacheFilePath);
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.BYTE.PAGE_MODE_TO_AND_READ_VALUE;
    }

    public byte getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            int pageIndex = getPage(filePosition);
            return byteBufferArray[pageIndex].get(getIndex(filePosition));
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the expect position value is: " + getIndex(filePosition) + " and the current capacity  value is: " + byteBufferArray[getPage(filePosition)].capacity() + " and the pageIndex is :" + getPage(filePosition) + e, e);
        } finally {
        }
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.BYTE.PAGE_STEP;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            Iterator<Entry<Integer, ByteBuffer>> iter = byteBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, ByteBuffer> entry = iter.next();
                entry.getValue().clear();
                iter.remove();
            }
            byteBufferArray = new ByteBuffer[1];
//        } catch (Exception e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected void initChild(int index, MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            byteBuffers.put(index, buffer.asReadOnlyBuffer());
            if (byteBufferArray.length <= index) {
                ByteBuffer[] temp = new ByteBuffer[index + 1];
                System.arraycopy(byteBufferArray, 0, temp, 0, byteBufferArray.length);
                byteBufferArray = temp;
            }
            byteBufferArray[index] = byteBuffers.get(index);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}