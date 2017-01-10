package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.exception.BIResourceInvalidException;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public class BIByteSingleFileNIOReader extends BIBaseSingleFileNIOReader implements ICubeByteReader {
    private ByteBuffer byteBuffer ;
    private ByteBuffer fakeBuffer;

    public BIByteSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public byte getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return byteBuffer.get((int)filePosition);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the file is: "+baseFile , e);
        } catch (NullPointerException e){
            initBuffer();
            if (byteBuffer == null){
                throw new RuntimeException("the file is released: "+baseFile , e);
            }
            return getSpecificValue(filePosition);
        }
    }

    @Override
    protected void setBufferInValid() {
        fakeBuffer = byteBuffer;
        byteBuffer = null;
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            if(byteBuffer!=null) {
                byteBuffer.clear();
                byteBuffer = null;
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
            byteBuffer = buffer.asReadOnlyBuffer();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
