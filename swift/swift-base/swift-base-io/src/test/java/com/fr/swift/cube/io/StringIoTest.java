package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.mem.StringMemIo;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class StringIoTest extends BaseIoTest {
    final byte[] bytes = new byte[r.nextInt(BOUND)];
    final String val;
    String basePath = cubesPath + "/string/";

    {
        r.nextBytes(bytes);
        val = new String(bytes);
    }

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child-overwrite");

        StringWriter writer = (StringWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.STRING, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        StringReader reader = (StringReader) Readers.build(location, new BuildConf(IoType.READ, DataType.STRING));
        String actual = reader.get(pos);

        assertEquals(val, actual);
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        StringWriter writer = (StringWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.STRING, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (StringWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.STRING, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        StringReader reader = (StringReader) Readers.build(location, new BuildConf(IoType.READ, DataType.STRING));
        String actual = reader.get(pos);
        String actual1 = reader.get(pos + 1);

        assertEquals(val, actual);
        assertEquals(val, actual1);
        reader.release();
    }

    @Override
    public void testMemPutThenGet() {
        StringMemIo stringMemIo = new StringMemIo();
        stringMemIo.put(pos, val);

        assertEquals(val, stringMemIo.get(pos));
        stringMemIo.release();
    }
}
