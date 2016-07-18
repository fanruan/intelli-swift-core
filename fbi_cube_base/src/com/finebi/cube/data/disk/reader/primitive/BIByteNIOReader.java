package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BIByteNIOReader extends BIBasicNIOReader<Byte> implements ICubeByteReader {

    private Map<Long, ByteBuffer> byteBuffers = new ConcurrentHashMap<Long, ByteBuffer>();


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

    @Override
    protected Byte getValue(Long page, int index) throws BIResourceInvalidException {
        if(isValid) {
            try {
                return byteBuffers.get(page).get(index);
            } catch (IndexOutOfBoundsException ex) {
                throw new IndexOutOfBoundsException(BIStringUtils.appendWithSpace("BI page value is:",
                        page.toString(), "the index value is", Integer.valueOf(index).toString()));
            } finally {
            }
        } else {
            throw new BIResourceInvalidException();
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
            Iterator<Entry<Long, ByteBuffer>> iter = byteBuffers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, ByteBuffer> entry = iter.next();
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
            byteBuffers.put(index, buffer.asReadOnlyBuffer());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}