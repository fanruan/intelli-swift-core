package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.cube.io.impl.fineio.input.LongFineIoReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongArrayWriter;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.util.Crasher;

import java.net.URI;

/**
 * @author yee
 * @date 2018/4/9
 */
public class LongArrayFineIoWriter implements LongArrayWriter {
    private LongWriter contentWriter;
    private LongWriter positionWriter;
    private IntWriter lengthWriter;

    private LongWriter lastPosWriter;

    private long curPos;

    private LongArrayFineIoWriter(LongWriter contentWriter, LongWriter positionWriter, IntWriter lengthWriter, LongWriter lastPosWriter) {
        this.contentWriter = contentWriter;
        this.positionWriter = positionWriter;
        this.lengthWriter = lengthWriter;
        this.lastPosWriter = lastPosWriter;
    }

    public static LongArrayWriter build(URI location, boolean isOverwrite) {
        LongArrayFineIoWriter bafw = getByteArrayFineIoWriter(location, isOverwrite);
        if (!isOverwrite) {
            bafw.curPos = getLastPosition(location);
        }
        return bafw;
    }

    private static LongArrayFineIoWriter getByteArrayFineIoWriter(URI location, boolean isOverwrite) {

        // 获得内容部分的byte类型Writer
        URI contentLocation = URI.create(location.getPath() + "/" + CONTENT);
        LongWriter contentWriter = LongFineIoWriter.build(contentLocation, isOverwrite);

        // 获得位置部分的long类型Writer
        URI positionLocation = URI.create(location.getPath() + "/" + POSITION);
        LongWriter positionWriter = LongFineIoWriter.build(positionLocation, isOverwrite);

        // 获得长度部分的int类型Writer
        URI lengthLocation = URI.create(location.getPath() + "/" + LENGTH);
        IntWriter lengthWriter = IntFineIoWriter.build(lengthLocation, isOverwrite);

        // 获得最后位置部分的long类型Writer
        URI lastPosLocation = URI.create(location.getPath() + "/" + LAST_POSITION);
        LongWriter lastPosWriter = LongFineIoWriter.build(lastPosLocation, true);

        return new LongArrayFineIoWriter(contentWriter, positionWriter, lengthWriter, lastPosWriter);
    }

    /**
     * last position是用于edit的场景
     * over write场景不用它
     *
     * @param location 位置
     * @return 上次写的位置
     */
    private static long getLastPosition(URI location) {
        try {
            URI lastPosLocation = URI.create(location.getPath() + "/" + LAST_POSITION);
            LongReader lastPosReader = LongFineIoReader.build(lastPosLocation);
            return lastPosReader.isReadable() ? lastPosReader.get(0) : 0;
        } catch (Exception e) {
            return Crasher.crash("cannot get last position", e);
        }
    }

    @Override
    public void flush() {
        contentWriter.flush();
        positionWriter.flush();
        lengthWriter.flush();

        lastPosWriter.put(0, curPos);
        lastPosWriter.flush();
    }

    @Override
    public void release() {
        contentWriter.release();
        positionWriter.release();
        lengthWriter.release();

        lastPosWriter.put(0, curPos);
        lastPosWriter.release();
    }

    @Override
    public void put(long pos, LongArray val) {
        if (val == null) {
            val = NULL_VALUE;
        }
        int len = val.size();
        positionWriter.put(pos, curPos);
        lengthWriter.put(pos, len);
        for (int i = 0; i < len; i++) {
            contentWriter.put(curPos + i, val.get(i));
        }

        curPos += len;
    }
}
