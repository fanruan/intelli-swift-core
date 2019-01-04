package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.mem.DoubleMemIo;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class DoubleIoTest extends BaseIoTest {
    double val = r.nextDouble();
    String basePath = cubesPath + "/double/";

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        DoubleWriter writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        DoubleReader reader = (DoubleReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.DOUBLE));

        assertEquals(val, reader.get(pos), 0);
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        DoubleWriter writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        DoubleReader reader = (DoubleReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.DOUBLE));

        assertEquals(val, reader.get(pos), 0);
        assertEquals(val, reader.get(pos + 1), 0);
        reader.release();
    }

    @Override
    public void testMemPutThenGet() {
        DoubleMemIo doubleMemIo = new DoubleMemIo();
        doubleMemIo.put(pos, val);

        assertEquals(val, doubleMemIo.get(pos), 0);
        doubleMemIo.release();
    }

}
