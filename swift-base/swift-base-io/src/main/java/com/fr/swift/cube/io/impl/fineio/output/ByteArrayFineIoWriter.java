package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.cube.io.impl.fineio.input.LongFineIoReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.ByteWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteArrayFineIoWriter implements ByteArrayWriter {
    private ByteWriter contentWriter;
    private LongWriter positionWriter;
    private IntWriter lengthWriter;

    private LongWriter lastPosWriter;

    private long curPos;

    private ByteArrayFineIoWriter(ByteWriter contentWriter, LongWriter positionWriter, IntWriter lengthWriter, LongWriter lastPosWriter) {
        this.contentWriter = contentWriter;
        this.positionWriter = positionWriter;
        this.lengthWriter = lengthWriter;
        this.lastPosWriter = lastPosWriter;
    }

    public static ByteArrayWriter build(URI location, boolean isOverwrite) {
        ByteArrayFineIoWriter bafw = getByteArrayFineIoWriter(location, isOverwrite);
        if (!isOverwrite) {
            bafw.curPos = getLastPosition(location);
        }
        return bafw;
    }

    private static ByteArrayFineIoWriter getByteArrayFineIoWriter(URI location, boolean isOverwrite) {

        // 获得内容部分的byte类型Writer
        URI contentLocation = URI.create(location.getPath() + "/" + CONTENT);
        ByteWriter contentWriter = ByteFineIoWriter.build(contentLocation, isOverwrite);

        // 获得位置部分的long类型Writer
        URI positionLocation = URI.create(location.getPath() + "/" + POSITION);
        LongWriter positionWriter = LongFineIoWriter.build(positionLocation, isOverwrite);

        // 获得长度部分的int类型Writer
        URI lengthLocation = URI.create(location.getPath() + "/" + LENGTH);
        IntWriter lengthWriter = IntFineIoWriter.build(lengthLocation, isOverwrite);

        // 获得最后位置部分的long类型Writer
        URI lastPosLocation = URI.create(location.getPath() + "/" + LAST_POSITION);
        LongWriter lastPosWriter = LongFineIoWriter.build(lastPosLocation, true);

        return new ByteArrayFineIoWriter(contentWriter, positionWriter, lengthWriter, lastPosWriter);
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
        lastPosWriter.put(0, curPos);
        IoUtil.release(lastPosWriter, contentWriter, positionWriter, lengthWriter);
        contentWriter = null;
        positionWriter = null;
        lengthWriter = null;
        lastPosWriter = null;
    }

    @Override
    public void put(long pos, byte[] val) {
        if (val == null) {
            val = NULL_VALUE;
        }
        int len = val.length;
        positionWriter.put(pos, curPos);
        lengthWriter.put(pos, len);
        for (int i = 0; i < len; i++) {
            contentWriter.put(curPos + i, val[i]);
        }

        curPos += len;
    }

    @Override
    public void resetContentPosition() {
        curPos = 0;
    }
}
