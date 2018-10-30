package com.fr.swift.segment.column.impl.base;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.location.ResourceLocation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class BitMapColumnTest {
    static final String BASE_PATH = "cubes/table/seg0/column";
    Random r = new Random();
    int size = 1000000;

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public void testPutThenGet() {
        MutableBitMap m = BitMaps.newRoaringMutable();
        for (int i = 0; i < size; i++) {
            m.add(r.nextInt(size << 1));
        }

        BitMapColumn bc = new BitMapColumn(new ResourceLocation(BASE_PATH + "/index/child"));

        int pos = 0;
        bc.putBitMapIndex(pos, m);
        bc.putNullIndex(m);
        bc.release();

        bc = new BitMapColumn(new ResourceLocation(BASE_PATH + "/index/child"));
        final ImmutableBitMap im = bc.getBitMapIndex(pos);
        final ImmutableBitMap nullIm = bc.getNullIndex();
        m.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                if (!im.contains(row) || !nullIm.contains(row)) {
                    Assert.fail();
                }
            }
        });
    }
}
