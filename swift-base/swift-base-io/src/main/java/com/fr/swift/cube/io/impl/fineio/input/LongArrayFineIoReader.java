package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongArrayReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.LongArrayWriter;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author yee
 * @date 2018/4/9
 */
public class LongArrayFineIoReader implements LongArrayReader {
    private LongReader contentReader;
    private LongReader positionReader;
    private IntReader lengthReader;

    private LongArrayFineIoReader(LongReader contentReader, LongReader positionReader, IntReader lengthReader) {
        this.contentReader = contentReader;
        this.positionReader = positionReader;
        this.lengthReader = lengthReader;
    }

    public static LongArrayFineIoReader build(URI location) {
        // 获得内容部分的byte类型reader
        URI contentLocation = URI.create(location.getPath() + "/" + LongArrayWriter.CONTENT);
        LongReader contentReader = LongFineIoReader.build(contentLocation);

        // 获得位置部分的long类型reader
        URI positionLocation = URI.create(location.getPath() + "/" + LongArrayWriter.POSITION);
        LongReader positionReader = LongFineIoReader.build(positionLocation);

        // 获得长度部分的int类型reader
        URI lengthLocation = URI.create(location.getPath() + "/" + LongArrayWriter.LENGTH);
        IntReader lengthReader = IntFineIoReader.build(lengthLocation);

        return new LongArrayFineIoReader(contentReader, positionReader, lengthReader);
    }

    @Override
    public boolean isReadable() {
        return contentReader != null && contentReader.isReadable();
    }

    @Override
    public LongArray get(long pos) {
        long start = positionReader.get(pos);
        int size = lengthReader.get(pos);
        if (size == 0) {
            return LongArrayWriter.NULL_VALUE;
        }
        LongArray longs = LongListFactory.createLongArray(size, NIOConstant.LONG.NULL_VALUE);
        for (int i = 0; i < size; i++) {
            longs.put(i, contentReader.get(start + i));
        }
        return longs;
    }

    @Override
    public void release() {
        IoUtil.release(contentReader, lengthReader, positionReader);
    }
}
