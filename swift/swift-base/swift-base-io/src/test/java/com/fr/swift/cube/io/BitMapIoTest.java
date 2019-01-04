package com.fr.swift.cube.io;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.mem.BitMapMemIo;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2017/11/6
 */
public class BitMapIoTest extends BaseIoTest {
    final MutableBitMap val = BitMaps.newRoaringMutable();
    int[] ints;
    String basePath = cubesPath + "/bitmap/";

    @Before
    public void setUp() throws Exception {
        ints = new int[BOUND];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = r.nextInt(BOUND);
            val.add(ints[i]);
        }
    }

    @Override
    public void testOverwritePutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child_overwrite");

        BitMapWriter writer = (BitMapWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BITMAP, WriteType.OVERWRITE));
        writer.put(pos, val);
        writer.release();

        BitMapReader reader = (BitMapReader) Readers.build(location, new BuildConf(IoType.READ, DataType.BITMAP));
        ImmutableBitMap actual = reader.get(pos);

        for (int i : ints) {
            if (!actual.contains(i)) {
                fail();
            }
        }

        reader.release();
    }

    @Override
    public void testPutThenGet() {
        IResourceLocation location = new ResourceLocation(basePath + "child");
        BitMapWriter writer = (BitMapWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BITMAP, WriteType.APPEND));
        writer.put(pos, val);
        writer.release();

        writer = (BitMapWriter) Writers.build(location, new BuildConf(IoType.WRITE, DataType.BITMAP, WriteType.APPEND));
        writer.put(pos + 1, val);
        writer.release();

        BitMapReader reader = (BitMapReader) Readers.build(location, new BuildConf(IoType.READ, DataType.BITMAP));
        ImmutableBitMap actual = reader.get(pos);
        ImmutableBitMap actual1 = reader.get(pos + 1);

        for (int i : ints) {
            if (!actual.contains(i) || !actual1.contains(i)) {
                fail();
            }
        }

        reader.release();
    }

    //    @Override
    public void testMemPutThenGet() {
        BitMapMemIo bitMapMemIo = new BitMapMemIo();

        bitMapMemIo.put(pos, val);

        assertEquals(val, bitMapMemIo.get(pos));
        bitMapMemIo.release();
    }
}