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

    public BIIntegerSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public int getSpecificValue(long filePosition) throws BIResourceInvalidException {
        try {
            return intBuffer.get((int)filePosition);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the file is: "+baseFile , e);
        }
    }

    @Override
    protected void releaseChild() {
        readWriteLock.writeLock().lock();
        try {
            if (intBuffer != null) {
                intBuffer.clear();
//                intBuffer = null;
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
