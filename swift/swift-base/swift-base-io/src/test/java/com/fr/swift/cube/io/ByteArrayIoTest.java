package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.ByteArrayWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class ByteArrayIoTest extends BaseIoTest {
    final byte[] val = new byte[r.nextInt(BOUND)];
    String basePath = cubesPath + "/bytearray/";

    {
        r.nextBytes(val);
    }

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        ByteArrayWriter writer = (ByteArrayWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BYTE_ARRAY, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        ByteArrayReader reader = (ByteArrayReader) Readers.build(location, new BuildConf(IoType.READ, DataType.BYTE_ARRAY));
        byte[] actual = reader.get(pos);

        assertEquals(val.length, actual.length);
        for (int i = 0; i < val.length; i++) {
            if (val[i] != actual[i]) {
                fail();
            }
        }

        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        ByteArrayWriter writer = (ByteArrayWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BYTE_ARRAY, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (ByteArrayWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BYTE_ARRAY, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        ByteArrayReader reader = (ByteArrayReader) Readers.build(location, new BuildConf(IoType.READ, DataType.BYTE_ARRAY));
        byte[] actual = reader.get(pos);
        byte[] actual1 = reader.get(pos + 1);

        assertEquals(val.length, actual.length);
        assertEquals(val.length, actual1.length);
        for (int i = 0; i < val.length; i++) {
            if (val[i] != actual[i] || val[i] != actual1[i]) {
                fail();
            }
        }

        reader.release();
    }
    /**
     * 木有byte数组的内存rw
     */
    @Override
    public void testMemPutThenGet() {
    }
}
