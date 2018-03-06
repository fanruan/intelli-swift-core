package com.fr.swift.cube.io;

import com.fr.swift.cube.io.impl.mem.DoubleMemIo;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class DoubleIoTest extends BaseIoTest {
    long pos = r.nextInt(BOUND);
    double val = r.nextDouble();
    String basePath = CUBES_PATH + "/double/";

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        DoubleWriter writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE));
        writer.put(pos, val);
        writer.release();

        DoubleReader reader = (DoubleReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.DOUBLE));

        assertEquals(val, reader.get(pos));
        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");

        DoubleWriter writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE, Types.WriteType.EDIT));
        writer.put(pos, val);
        writer.release();

        writer = (DoubleWriter) Writers.build(location, new BuildConf(Types.IoType.WRITE, Types.DataType.DOUBLE, Types.WriteType.EDIT));
        writer.put(pos + 1, val);
        writer.release();

        DoubleReader reader = (DoubleReader) Readers.build(location, new BuildConf(Types.IoType.READ, Types.DataType.DOUBLE));

        assertEquals(val, reader.get(pos));
        assertEquals(val, reader.get(pos + 1));
        reader.release();
    }

    @Override
    public void testMemPutThenGet() {
        DoubleMemIo doubleMemIo = new DoubleMemIo();
        doubleMemIo.put(pos, val);

        assertEquals(val, doubleMemIo.get(pos));
        doubleMemIo.release();
    }

}
