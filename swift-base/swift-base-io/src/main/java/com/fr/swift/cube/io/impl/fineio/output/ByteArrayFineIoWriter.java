package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.cube.io.impl.BaseByteArrayWriter;
import com.fr.swift.cube.io.impl.fineio.input.LongFineIoReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.ByteWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteArrayFineIoWriter extends BaseByteArrayWriter {

    private ByteArrayFineIoWriter(ByteWriter dataWriter, LongWriter posWriter, IntWriter lenWriter, LongWriter lastPosWriter, LongReader lastPosReader, boolean isOverwrite) {
        super(dataWriter, posWriter, lenWriter, isOverwrite, lastPosWriter, lastPosReader);
    }

    public static ByteArrayWriter build(URI location, boolean isOverwrite) {
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
        LongWriter lastPosWriter = isOverwrite ? null : LongFineIoWriter.build(lastPosLocation, true);
        LongReader lastPosReader = isOverwrite ? null : LongFineIoReader.build(lastPosLocation);

        return new ByteArrayFineIoWriter(contentWriter, positionWriter, lengthWriter, lastPosWriter, lastPosReader, isOverwrite);
    }
}
