package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongArrayReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;

/**
 * @author yee
 * @date 2018/4/9
 */
public class LongArrayFineIoReader extends BaseFineIoReader implements LongArrayReader {
    private LongReader contentReader;
    private LongReader positionReader;
    private IntReader lengthReader;

    private LongArrayFineIoReader(LongReader contentReader, LongReader positionReader, IntReader lengthReader) {
        this.contentReader = contentReader;
        this.positionReader = positionReader;
        this.lengthReader = lengthReader;
    }

    public static LongArrayFineIoReader build(IResourceLocation location) {
        // 获得内容部分的byte类型reader
        IResourceLocation contentLocation = location.buildChildLocation(CONTENT);
        LongReader contentReader = LongFineIoReader.build(contentLocation);

        // 获得位置部分的long类型reader
        IResourceLocation positionLocation = location.buildChildLocation(POSITION);
        LongReader positionReader = LongFineIoReader.build(positionLocation);

        // 获得长度部分的int类型reader
        IResourceLocation lengthLocation = location.buildChildLocation(LENGTH);
        IntReader lengthReader = IntFineIoReader.build(lengthLocation);

        return new LongArrayFineIoReader(contentReader, positionReader, lengthReader);
    }

    @Override
    public boolean isReadable() {
        return contentReader != null && contentReader.isReadable();
    }

    @Override
    public long getLastPosition(long pos) {
        if (pos == 0) {
            return 0;
        }
        long start = positionReader.get(pos - 1);
        int len = lengthReader.get(pos - 1);
        return start + len;
    }

    @Override
    public LongArray get(long pos) {
        long start = positionReader.get(pos);
        int size = lengthReader.get(pos);
        if (size == 0) {
            return NULL_VALUE;
        }
        LongArray longs = LongListFactory.createLongArray(size, NIOConstant.LONG.NULL_VALUE);
        for (int i = 0; i < size; i++) {
            longs.put(i, contentReader.get(start + i));
        }
        return longs;
    }
}
