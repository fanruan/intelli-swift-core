package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.ByteWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/3
 */
public class ByteIoTest extends BaseIoTest {
    byte val = (byte) r.nextInt(BOUND);
    String basePath = cubesPath + "/byte/";

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        ByteWriter writer = (ByteWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.BYTE, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        ByteReader reader = (ByteReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.BYTE));

        assertEquals(val, reader.get(pos));
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        ByteWriter bw = (ByteWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.BYTE, WriteType.APPEND));
        bw.put(pos, val);
        bw.release();

        bw = (ByteWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.BYTE, WriteType.APPEND));
        bw.put(pos + 1, val);
        bw.release();

        ByteReader br = (ByteReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.BYTE));

        assertEquals(val, br.get(pos));
        assertEquals(val, br.get(pos + 1));
        br.release();
    }

    /**
     * 木有byte的内存rw
     */
    @Override
    public void testMemPutThenGet() {
    }

}
