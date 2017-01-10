package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeDoubleReader;
import com.finebi.cube.exception.BIResourceInvalidException;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public class BIDoubleSingleFileNIOReader extends BIBaseSingleFileNIOReader implements ICubeDoubleReader {
    private DoubleBuffer doubleBuffer ;
    private DoubleBuffer fakeBuffer;

    public BIDoubleSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public double getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return doubleBuffer.get((int)filePosition);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the file is: "+baseFile , e);
        } catch (NullPointerException e){
            initBuffer();
            if (doubleBuffer == null){
                throw new RuntimeException("the file is released: "+baseFile , e);
            }
            return getSpecificValue(filePosition);
        }
    }

    protected void setBufferInValid() {
        fakeBuffer = doubleBuffer;
        doubleBuffer = null;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            if (doubleBuffer != null) {
                doubleBuffer.clear();
                doubleBuffer = null;
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
            doubleBuffer = buffer.asDoubleBuffer();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
