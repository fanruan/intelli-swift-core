package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.impl.BaseByteArrayReader;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.ByteArrayWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteArrayFineIoReader extends BaseByteArrayReader {

    public ByteArrayFineIoReader(ByteReader dataReader, LongReader posReader, IntReader lenReader) {
        super(dataReader, posReader, lenReader);
    }

    public static ByteArrayReader build(URI location) {
        // 获得内容部分的byte类型reader
        URI contentLocation = URI.create(location.getPath() + "/" + ByteArrayWriter.CONTENT);
        ByteReader contentReader = ByteFineIoReader.build(contentLocation);

        // 获得位置部分的long类型reader
        URI positionLocation = URI.create(location.getPath() + "/" + ByteArrayWriter.POSITION);
        LongReader positionReader = LongFineIoReader.build(positionLocation);

        // 获得长度部分的int类型reader
        URI lengthLocation = URI.create(location.getPath() + "/" + ByteArrayWriter.LENGTH);
        IntReader lengthReader = IntFineIoReader.build(lengthLocation);

        return new ByteArrayFineIoReader(contentReader, positionReader, lengthReader);
    }
}