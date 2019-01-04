package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.mem.IntMemIo;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class IntIoTest extends BaseIoTest {
    int val = r.nextInt(BOUND);
    String basePath = cubesPath + "/int/";

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        IntWriter writer = (IntWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.INT, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        IntReader reader = (IntReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.INT));

        assertEquals(val, reader.get(pos));
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        IntWriter writer = (IntWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.INT, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (IntWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.INT, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        IntReader reader = (IntReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.INT));

        assertEquals(val, reader.get(pos));
        assertEquals(val, reader.get(pos + 1));
        reader.release();
    }

    @Override
    public void testMemPutThenGet() {
        IntMemIo intMemIo = new IntMemIo();
        intMemIo.put(pos, val);

        assertEquals(val, intMemIo.get(pos));
        intMemIo.release();
    }

}
