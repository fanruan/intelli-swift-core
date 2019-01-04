package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.mem.LongMemIo;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class LongIoTest extends BaseIoTest {
    long val = r.nextLong();
    String basePath = cubesPath + "/long/";

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        LongWriter writer = (LongWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.LONG, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        LongReader reader = (LongReader) Readers.build(location, new BuildConf(IoType.READ, DataType.LONG));

        assertEquals(val, reader.get(pos));
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        LongWriter writer = (LongWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.LONG, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (LongWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.LONG, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        LongReader reader = (LongReader) Readers.build(location, new BuildConf(IoType.READ, DataType.LONG));

        assertEquals(val, reader.get(pos));
        assertEquals(val, reader.get(pos + 1));
        reader.release();
    }

    @Override
    public void testMemPutThenGet() {
        LongMemIo longMemIo = new LongMemIo();
        longMemIo.put(pos, val);

        assertEquals(val, longMemIo.get(pos));
        longMemIo.release();
    }

}
