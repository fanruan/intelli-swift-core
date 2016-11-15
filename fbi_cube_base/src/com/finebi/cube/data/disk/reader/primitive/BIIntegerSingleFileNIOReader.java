package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.exception.BIResourceInvalidException;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public class BIIntegerSingleFileNIOReader extends BIBaseSingleFileNIOReader implements ICubeIntegerReader {
    private IntBuffer intBuffer ;
    private IntBuffer fakeBuffer;
    public BIIntegerSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public int getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return intBuffer.get((int)filePosition);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the file is: "+baseFile , e);
        } catch (NullPointerException e){
            initBuffer();
            if (intBuffer == null){
                throw new RuntimeException("the file is released: "+baseFile , e);
            }
            return getSpecificValue(filePosition);
        }
    }

    @Override
    protected void setBufferInValid() {
        fakeBuffer = intBuffer;
        intBuffer = null;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            if (intBuffer != null) {
                intBuffer.clear();
                intBuffer = null;
            }
            if (fakeBuffer != null) {
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
            intBuffer = buffer.asIntBuffer();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
