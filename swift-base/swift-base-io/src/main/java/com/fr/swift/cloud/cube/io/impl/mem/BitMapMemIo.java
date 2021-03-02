package com.fr.swift.cloud.cube.io.impl.mem;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.input.BitMapReader;
import com.fr.swift.cloud.cube.io.output.BitMapWriter;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class BitMapMemIo extends SwiftObjectMemIo<ImmutableBitMap> implements BitMapReader, BitMapWriter {
    @Override
    public void resetContentPosition() {
        // nothing
    }
}
