package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.exception.BIResourceInvalidException;

import java.io.File;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public class BILongSingleFileNIOReader extends BIBaseSingleFileNIOReader implements ICubeLongReader {
    private LongBuffer longBuffer ;
    private LongBuffer fakeBuffer;

    public BILongSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public long getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return longBuffer.get((int)filePosition);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the file is: "+baseFile , e);
        } catch (NullPointerException e){
            initBuffer();
            if (longBuffer == null){
                throw new RuntimeException("the file is released: "+baseFile , e);
            }
            return getSpecificValue(filePosition);
        }
    }

    protected void setBufferInValid() {
        fakeBuffer = longBuffer;
        longBuffer = null;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            if (longBuffer != null) {
                longBuffer.clear();
                longBuffer = null;
            }
            if (fakeBuffer != null){
                fakeBuffer.clear();
                fakeBuffer = null;
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    protected void initChild(MappedByteBuffer buffer) {
        readWriteLock.writeLock().lock();
        try {
            longBuffer = buffer.asLongBuffer();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
